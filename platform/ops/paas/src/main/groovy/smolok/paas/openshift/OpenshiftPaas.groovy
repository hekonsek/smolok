/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package smolok.paas.openshift

import net.smolok.lib.download.DownloadManager
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

    private static final OPENSHIFT_DISTRO = 'openshift-origin-server-v1.3.0-rc1-ac0bb1bf6a629e0c262f04636b8cf2916b16098c-linux-64bit'

    private static final OPENSHIFT_DISTRO_ARCHIVE = "${OPENSHIFT_DISTRO}.tar.gz"

    private static final OPENSHIFT_DOWNLOAD_URL = new URL("https://github.com/openshift/origin/releases/download/v1.3.0-rc1/${OPENSHIFT_DISTRO_ARCHIVE}")

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
        downloadManager.download(new BinaryCoordinates(OPENSHIFT_DOWNLOAD_URL, OPENSHIFT_DISTRO_ARCHIVE, OPENSHIFT_DISTRO))
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
                def serverPath = Paths.get(downloadManager.downloadedFile(OPENSHIFT_DISTRO).absolutePath, OPENSHIFT_DISTRO, 'openshift').toFile().absolutePath
                processManager.executeAsync(serverPath, 'start')
            } else {
                def serverPath = Paths.get(downloadManager.downloadedFile(OPENSHIFT_DISTRO).absolutePath, OPENSHIFT_DISTRO, 'openshift').toFile().absolutePath
                processManager.executeAsync('sudo', serverPath, 'start')
                await().atMost(60, SECONDS).until({isNotLoggedIntoProject()} as Callable<Boolean>)
                await().atMost(60, SECONDS).until({
                    !oc('login https://localhost:8443 -u admin -p admin --insecure-skip-tls-verify=true').first().
                            startsWith('Error from server: User "admin" cannot get users at the cluster scope')
                } as Callable<Boolean>)
                oc('new-project smolok')
                await().atMost(60, SECONDS).until({isOsStarted()} as Callable<Boolean>)
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
            processManager.execute('sudo', 'kill', pid)
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
        def statusOutput = oc(OS_STATUS_COMMAND).first()
        statusOutput.contains('You must be logged in to the server') || statusOutput.contains('Missing or incomplete configuration info')
    }

    private isOsStarted() {
        oc(OS_STATUS_COMMAND).first().startsWith('In project ')
    }

    private oc(String cmd) {
        processManager.execute(command(Paths.get(downloadManager.downloadedFile('openshift').absolutePath, 'openshift-origin-server-v1.3.0-rc1-ac0bb1bf6a629e0c262f04636b8cf2916b16098c-linux-64bit', 'oc').toFile().absolutePath + ' ' + cmd))
    }

}
