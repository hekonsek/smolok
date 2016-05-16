package smolok.paas.openshift

import smolok.lib.process.ProcessManager
import smolok.paas.Paas

import static org.slf4j.LoggerFactory.getLogger
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

    private final static OS_START_COMMAND = 'docker start openshift-server'

    private final static DOCKER_PS = 'docker ps -f name=openshift-server'

    private final static DOCKER_PS_ALL = "${DOCKER_PS} -a"

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
        processManager.execute(command(DOCKER_PS)).size() > 1
    }

    @Override
    void start() {
        if(!isStarted()) {
            if(isProvisioned()) {
                def output = processManager.execute(command(OS_START_COMMAND))
                LOG.debug('Starting existing OpenShift instance. Result: {}', output)
            } else {
                def output = processManager.execute(command(OS_PROVISION_COMMAND))
                LOG.debug('No OpenShift instance found - provisioning new one. Result: {}', output)
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
        println 'docker rm openshift-server'.execute().text
    }

}
