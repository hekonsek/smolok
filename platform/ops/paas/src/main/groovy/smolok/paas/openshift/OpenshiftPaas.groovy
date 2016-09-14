package smolok.paas.openshift

import net.smolok.lib.download.DownloadManager
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.lang3.Validate
import smolok.lib.process.ProcessManager
import smolok.lib.vertx.AmqpProbe
import smolok.paas.Paas
import smolok.paas.ServiceEndpoint

import java.nio.file.Paths
import java.util.concurrent.Callable

import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.SECONDS
import static net.smolok.lib.download.DownloadManager.BinaryCoordinates
import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties
import static smolok.lib.process.ExecutorBasedProcessManager.command

class OpenshiftPaas implements Paas {

    // Logging

    private final static LOG = getLogger(OpenshiftPaas.class)

    // Constants

    private static final OPENSHIFT_DOWNLOAD_URL = new URL('https://github.com/openshift/origin/releases/download/v1.3.0-rc1/openshift-origin-server-v1.3.0-rc1-ac0bb1bf6a629e0c262f04636b8cf2916b16098c-linux-64bit.tar.gz')

    // OpenShift commands constants

    private final static OS_STATUS_COMMAND = 'status'

    private final static OS_GET_SERVICES_COMMAND = 'get service'

    // Collaborators

    private final DownloadManager downloadManager

    private final ProcessManager processManager

    private final AmqpProbe amqpProbe

    // Constructors

    OpenshiftPaas(DownloadManager downloadManager, ProcessManager processManager, AmqpProbe amqpProbe) {
        this.downloadManager = downloadManager
        this.processManager = processManager
        this.amqpProbe = amqpProbe
    }

    // Platform operations

    void init() {
        downloadManager.download(new BinaryCoordinates(OPENSHIFT_DOWNLOAD_URL, 'openshift-origin-server-v1.3.0-rc1-ac0bb1bf6a629e0c262f04636b8cf2916b16098c-linux-64bit.tar.gz', 'openshift'))
    }

    @Override
    boolean isProvisioned() {
        new File('.').list().find{ it.startsWith('openshift.local') }
    }

    @Override
    boolean isStarted() {

        def eventBusOutput = oc(OS_GET_SERVICES_COMMAND).find {
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
                def serverPath = Paths.get(downloadManager.downloadedFile('openshift').absolutePath, 'openshift-origin-server-v1.3.0-rc1-ac0bb1bf6a629e0c262f04636b8cf2916b16098c-linux-64bit', 'openshift').toFile().absolutePath
                processManager.executeAsync(serverPath, 'start')
            } else {
                def serverPath = Paths.get(downloadManager.downloadedFile('openshift').absolutePath, 'openshift-origin-server-v1.3.0-rc1-ac0bb1bf6a629e0c262f04636b8cf2916b16098c-linux-64bit', 'openshift').toFile().absolutePath
                println processManager.executeAsync('sudo', serverPath, 'start')
                println 'xxxxxxx'
                await().atMost(60, SECONDS).until({println processManager.execute('ps', 'aux').findAll{it.contains('openshift start')}; println oc('get pod');isNotLoggedIntoProject()} as Callable<Boolean>)
                println 'yyyyyyy'
                Thread.sleep(15000)
                oc('login https://localhost:8443 -u admin -p admin --insecure-skip-tls-verify=true')
                oc('new-project smolok')
                await().atMost(60, SECONDS).until({println oc('get pod'); isOsStarted()} as Callable<Boolean>)
                def smolokVersion = artifactVersionFromDependenciesProperties('net.smolok', 'smolok-paas')
                Validate.isTrue(smolokVersion.present, 'Smolok version cannot be resolved.')
                oc("new-app smolok/eventbus:${smolokVersion.get()}")
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
        processManager.execute(command('ps aux')).findAll{ it.contains('openshift start') }.each {
            def pid = it.split(/\s+/)[1]
            processManager.execute('kill', pid)
        }
    }

    @Override
    void reset() {
        stop()

        def openshiftDirectory = SystemUtils.userDir.absolutePath

        processManager.execute('mount').each {
            def volume = it.split(' ')[2]
            if(volume.startsWith(openshiftDirectory)) {
                processManager.execute(command("sudo umount ${volume}"))
            }
        }

        new File('.').listFiles().each {
            if(it.name.startsWith('openshift.local.')) {
                processManager.execute(command("sudo rm -rf ${it.name}"))
            }
        }
    }

    @Override
    List<ServiceEndpoint> services() {
        def output = oc(OS_GET_SERVICES_COMMAND)
        def servicesOutput = output.subList(1, output.size())
        servicesOutput.collect{ it.split(/\s+/) }.collect {
            new ServiceEndpoint(it[0], it[1], it[3].replaceFirst('/.+', '').toInteger())
        }
    }

    // Helpers

    private isNotLoggedIntoProject() {
        def yyy = oc(OS_STATUS_COMMAND).first()
                yyy.contains('You must be logged in to the server') || yyy.contains('Missing or incomplete configuration info')
    }

    private isNotStarted() {
        oc(OS_STATUS_COMMAND).first().startsWith('The connection to the server')
    }

    private isOsStarted() {
        oc(OS_STATUS_COMMAND).first().startsWith('In project ')
    }

    private oc(String cmd) {
        processManager.execute(command(Paths.get(downloadManager.downloadedFile('openshift').absolutePath, 'openshift-origin-server-v1.3.0-rc1-ac0bb1bf6a629e0c262f04636b8cf2916b16098c-linux-64bit', 'oc').toFile().absolutePath + ' ' + cmd))
    }

}
