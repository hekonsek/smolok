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
import java.util.concurrent.TimeoutException

import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.MINUTES
import static java.util.concurrent.TimeUnit.SECONDS
import static net.smolok.lib.download.DownloadManager.BinaryCoordinates
import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.Awaitilities.condition
import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties
import static smolok.lib.process.Command.cmd
import static smolok.lib.process.CommandBuilder.sudo

class OpenShiftPaas implements Paas {

    // Logging

    private final static LOG = getLogger(OpenShiftPaas.class)

    // Constants

    private static final OPENSHIFT_DISTRO = 'openshift-origin-server-v1.3.0-rc1-ac0bb1bf6a629e0c262f04636b8cf2916b16098c-linux-64bit'

    private static final OPENSHIFT_DISTRO_ARCHIVE = "${OPENSHIFT_DISTRO}.tar.gz"

    private static final OPENSHIFT_DOWNLOAD_URL = new URL("https://github.com/openshift/origin/releases/download/v1.3.0-rc1/${OPENSHIFT_DISTRO_ARCHIVE}")

    // OpenShift commands constants

    private final static OC_STATUS = 'status'

    private final static OC_GET_SERVICE = 'get service'

    // Collaborators

    private final DownloadManager downloadManager

    private final ProcessManager processManager

    private final AmqpProbe amqpProbe

    private final def openshiftHome = Paths.get(SystemUtils.getUserHome().absolutePath, '.smolok', 'openshift').toFile()

    // Cached variables

    private final def startOpenShiftCommand

    private final def ocPath

    // Constructors

    OpenShiftPaas(DownloadManager downloadManager, ProcessManager processManager, AmqpProbe amqpProbe) {
        this.downloadManager = downloadManager
        this.processManager = processManager
        this.amqpProbe = amqpProbe

        def serverPath = Paths.get(downloadManager.downloadedFile(OPENSHIFT_DISTRO).absolutePath, OPENSHIFT_DISTRO, 'openshift').toFile().absolutePath
        startOpenShiftCommand = sudo(serverPath, 'start').workingDirectory(openshiftHome).build()
        ocPath = downloadManager.fileFromExtractedDirectory("${OPENSHIFT_DISTRO}/${OPENSHIFT_DISTRO}", 'oc').absolutePath
    }

    // Platform operations

    void init() {
        openshiftHome.mkdirs()
        downloadManager.download(new BinaryCoordinates(OPENSHIFT_DOWNLOAD_URL, OPENSHIFT_DISTRO_ARCHIVE, OPENSHIFT_DISTRO))
    }

    @Override
    boolean isProvisioned() {
        openshiftHome.list().find { it.startsWith('openshift.local') }
    }

    @Override
    boolean isStarted() {
        def eventBusOutput = oc(OC_GET_SERVICE).find {
            it.startsWith('eventbus')
        }
        if (eventBusOutput == null) {
            return false
        }
        def eventBusOutputParts = eventBusOutput.split(/\s+/)
        amqpProbe.canSendMessageTo(eventBusOutputParts[1], eventBusOutputParts[3].replaceFirst('/.+', '').toInteger())
    }

    @Override
    void start() {
        if (!isStarted()) {
            def openshiftStartJob
            try {
                def isProvisioned = isProvisioned()
                openshiftStartJob = processManager.executeAsync(startOpenShiftCommand)
                if (!isProvisioned) {
                    LOG.debug('OpenShift is not provisioned. Started provisioning...')
                    await('login prompt is displayed').atMost(60, SECONDS).until(condition { loginPromptIsDisplayed() })
                    await().atMost(60, SECONDS).until({
                        def loginOutput = oc('login https://localhost:8443 -u admin -p admin --insecure-skip-tls-verify=true').first()
                        !loginOutput.startsWith('Error from server: User "admin" cannot get users at the cluster scope') &&
                                !loginOutput.startsWith('error: dial tcp')
                    } as Callable<Boolean>)
                    oc('new-project smolok')
                    await().atMost(60, SECONDS).until({ isOsStarted() } as Callable<Boolean>)
                    def smolokVersion = artifactVersionFromDependenciesProperties('net.smolok', 'smolok-paas')
                    Validate.isTrue(smolokVersion.present, 'Smolok version cannot be resolved.')
                    oc("new-app smolok/eventbus:${smolokVersion.get()}")
                }
                LOG.debug('Waiting for the event bus to start...')
                await().atMost(3, MINUTES).until({ isStarted() } as Callable<Boolean>)
                LOG.debug('Event bus has been started.')
            } finally {
                if(openshiftStartJob != null) {
                    LOG.debug('Collecting possible exceptions from OpenShift start job.')
                    try {
                        openshiftStartJob.get(1, SECONDS)
                    } catch (TimeoutException e) {
                        LOG.debug('OpenShift process has been started without exceptions.')
                    }
                }
            }
        } else {
            LOG.debug('OpenShift already running - no need to start it.')
        }
    }

    @Override
    void stop() {
        processManager.execute(sudo('ps aux').build()).findAll { it.contains('openshift start') }.each {
            def pid = it.split(/\s+/)[1]
            processManager.execute(sudo('kill', pid).build())
        }
    }

    @Override
    void reset() {
        stop()

        processManager.execute(sudo('mount').build()).each {
            def volume = it.split(' ')[2]
            if (volume.startsWith(openshiftHome.absolutePath)) {
                def umountOutput = processManager.execute(sudo("umount ${volume}").build())
                Validate.isTrue(umountOutput.isEmpty(), "Problem with unmounting volume: ${umountOutput}")
            }
        }

        openshiftHome.listFiles().each {
            if (it.name.startsWith('openshift.local.')) {
                def rmOutput = processManager.execute(sudo("rm -rf ${it.absolutePath}").build())
                Validate.isTrue(rmOutput.isEmpty(), "Problem with removing OpenShift installation: ${rmOutput}")
            }
        }
    }

    @Override
    List<ServiceEndpoint> services() {
        def output = oc(OC_GET_SERVICE)
        def servicesOutput = output.subList(1, output.size())
        servicesOutput.collect { it.split(/\s+/) }.collect {
            new ServiceEndpoint(it[0], it[1], it[3].replaceFirst('/.+', '').toInteger())
        }
    }

    // Helpers

    private loginPromptIsDisplayed() {
        def statusOutput = oc(OC_STATUS).first()
        statusOutput.contains('You must be logged in to the server') || statusOutput.contains('Missing or incomplete configuration info')
    }

    private isOsStarted() {
        oc(OC_STATUS).first().startsWith('In project ')
    }

    private oc(String command) {
        processManager.execute(cmd("${ocPath} ${command}"))
    }

}
