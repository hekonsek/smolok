package smolok.paas.openshift

import org.apache.commons.lang3.Validate
import smolok.lib.process.ProcessManager
import smolok.lib.vertx.AmqpProbe
import smolok.paas.Paas
import smolok.paas.ServiceEndpoint

import java.util.concurrent.Callable

import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.SECONDS
import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties
import static smolok.lib.process.ExecutorBasedProcessManager.command

class OpenshiftPaas implements Paas {

    // Logging

    private final static def LOG = getLogger(OpenshiftPaas.class)

    // Docker commands constants

    private final static OS_PROVISION_COMMAND =
            '''run -d --name openshift-server --privileged --pid=host --net=host
            -v /:/rootfs:ro -v /var/run:/var/run:rw -v /sys:/sys -v /var/lib/docker:/var/lib/docker:rw
            -v /var/lib/origin/openshift.local.volumes:/var/lib/origin/openshift.local.volumes
            openshift/origin start'''

    private final static OS_STATUS_COMMAND = 'exec openshift-server oc status'

    private final static OS_START_COMMAND = 'start openshift-server'

    private final static OS_REMOVE_COMMAND = 'rm openshift-server'

    private final static OS_GET_SERVICES_COMMAND = 'exec openshift-server oc get service'

    private final static DOCKER_PS_ALL = "ps -a -f name=openshift-server"

    // Collaborators

    private final ProcessManager processManager

    private final AmqpProbe amqpProbe

    // Constructors
    OpenshiftPaas(ProcessManager processManager, AmqpProbe amqpProbe) {
        this.processManager = processManager
        this.amqpProbe = amqpProbe
    }

    // Platform operations

    @Override
    boolean isProvisioned() {
        dockerRun(DOCKER_PS_ALL).size() > 1
    }

    @Override
    boolean isStarted() {
        def eventBusOutput = dockerRun(OS_GET_SERVICES_COMMAND).find {
            it.startsWith('eventbus')
        }
        if(eventBusOutput == null) {
            return false
        }
        def eventBusOutputParts = eventBusOutput.split(/\s+/)
        amqpProbe.canSendMessageTo(eventBusOutputParts[1], eventBusOutputParts[3].replaceFirst('/.+', '').toInteger())
    }

    @Override
    void start() {
        if(!isStarted()) {
            if(isProvisioned()) {
                dockerRun(OS_START_COMMAND)
            } else {
                dockerRun(OS_PROVISION_COMMAND)
                await().atMost(60, SECONDS).until({isOsStarted()} as Callable<Boolean>)
                def smolokVersion = artifactVersionFromDependenciesProperties('smolok', 'smolok-paas')
                Validate.isTrue(smolokVersion.present, 'Smolok version cannot be resolved.')
                dockerRun("exec openshift-server oc new-app smolok/eventbus:${smolokVersion.get()}")
            }
            LOG.debug('Waiting for the event bus to start...')
            await().atMost(120, SECONDS).until({isStarted()} as Callable<Boolean>)
            LOG.debug('Event bus has been started.')
        } else {
            LOG.debug('OpenShift already running - no need to start it.')
        }
    }

    @Override
    void stop() {
        dockerRun('ps -q').collect {
            processManager.executeAsync(command("docker stop ${it}"))
        }.collect { it.get() }
    }

    @Override
    void reset() {
        stop()
        dockerRun(OS_REMOVE_COMMAND)
    }

    @Override
    List<ServiceEndpoint> services() {
        def output = dockerRun(OS_GET_SERVICES_COMMAND)
        def servicesOutput = output.subList(1, output.size())
        servicesOutput.collect{ it.split(/\s+/) }.collect {
            new ServiceEndpoint(it[0], it[1], it[3].replaceFirst('/.+', '').toInteger())
        }
    }

    // Helpers

    private isOsStarted() {
        dockerRun(OS_STATUS_COMMAND).first().startsWith('In project ')
    }

    private dockerRun(String cmd) {
        processManager.execute(command("docker ${cmd}"))
    }

}
