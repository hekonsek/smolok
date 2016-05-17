package smolok.paas.openshift

import org.apache.commons.lang3.Validate
import smolok.lib.process.ProcessManager
import smolok.paas.Paas

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
            '''docker run -d --name openshift-server --privileged --pid=host --net=host
            -v /:/rootfs:ro -v /var/run:/var/run:rw -v /sys:/sys -v /var/lib/docker:/var/lib/docker:rw
            -v /var/lib/origin/openshift.local.volumes:/var/lib/origin/openshift.local.volumes
            openshift/origin start'''

    private final static OS_STATUS_COMMAND = 'docker exec openshift-server oc status'

    private final static OS_START_COMMAND = 'docker start openshift-server'

    private final static OS_REMOVE_COMMAND = 'docker rm openshift-server'

    private final static DOCKER_PS_ALL = "docker ps -a -f name=openshift-server"

    // Collaborators

    private final ProcessManager processManager

    // Constructors

    OpenshiftPaas(ProcessManager processManager) {
        this.processManager = processManager
    }

    // Platform operations

    @Override
    boolean isProvisioned() {
        processManager.execute(command(DOCKER_PS_ALL)).size() > 1
    }

    @Override
    boolean isStarted() {
        processManager.execute(command('docker exec -t openshift-server oc get service')).find {
            it.startsWith('eventbus')
        } != null
    }

    @Override
    void start() {
        if(!isStarted()) {
            if(isProvisioned()) {
                processManager.execute(command(OS_START_COMMAND))
            } else {
                processManager.execute(command(OS_PROVISION_COMMAND))
                await().atMost(60, SECONDS).until({isOsStarted()} as Callable<Boolean>)
                def smolokVersion = artifactVersionFromDependenciesProperties('smolok', 'smolok-paas')
                Validate.isTrue(smolokVersion.present, 'Smolok version cannot be resolved.')
                processManager.execute(command("docker exec openshift-server oc new-app smolok/eventbus:${smolokVersion.get()}"))
            }
        } else {
            LOG.debug('OpenShift already running - no need to start it.')
        }
    }

    @Override
    void stop() {
        processManager.execute(command('docker ps -q')).collect {
            processManager.executeAsync(command("docker stop ${it}"))
        }.collect { it.get() }
    }

    @Override
    void reset() {
        stop()
        processManager.execute(command(OS_REMOVE_COMMAND))
    }

    // Helpers

    private boolean isOsStarted() {
        def osStatus = processManager.execute(command(OS_STATUS_COMMAND)).first()
        osStatus.startsWith('In project ')
    }

}
