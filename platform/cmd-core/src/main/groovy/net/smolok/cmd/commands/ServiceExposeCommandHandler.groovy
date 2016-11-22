package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommandHandler
import net.smolok.cmd.spi.OutputSink
import net.smolok.paas.Paas
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.Validate
import smolok.lib.docker.ContainerBuilder
import smolok.lib.docker.Docker
import smolok.lib.process.ProcessManager

import static com.google.common.base.Charsets.UTF_8
import static com.google.common.io.Files.append
import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties
import static smolok.lib.process.Command.cmd

class ServiceExposeCommandHandler extends BaseCommandHandler {

    // Collaborators

    private final Paas paas

    private final ProcessManager processManager

    private final Docker docker

    // Constructors

    ServiceExposeCommandHandler(Paas paas, ProcessManager processManager, Docker docker) {
        super('service-expose')
        this.paas = paas
        this.processManager = processManager
        this.docker = docker
    }

    // CommandHandler operations

    @Override
    void handle(OutputSink outputSink, String commandId, String... command) {
        def serviceName = command[1]
        def serviceInterface = command[2]

        if(! new File('/root/.ssh/id_rsa.pub').exists()) {
            outputSink.out(commandId, 'Autossh key pair not found, generating new one...')
            def keyGenOutput = processManager.execute(cmd('ssh-keygen -b 2048 -t rsa -q -N ""'))
            Validate.isTrue(keyGenOutput.empty, "Problems while generating autossh key pair:\n${keyGenOutput}")

            append(IOUtils.toString(new FileInputStream('/root/.ssh/id_rsa.pub')), new File('/root/.ssh/authorized_keys'), UTF_8)
            processManager.execute(cmd('chmod og-wx /root/.ssh/authorized_keys'))
            outputSink.out(commandId, 'Generated autossh key pair.')
        }

        def service = paas.services().find{ it.name == serviceName }
        def smolokVersion = artifactVersionFromDependenciesProperties('net.smolok', 'smolok-paas')
        def tunnel = "${serviceInterface}:${service.port}:${service.host}:${service.port}"
        docker.startService(new ContainerBuilder("smolok/autossh:${smolokVersion}").name("${serviceName}-autossh").net('host').volumes(['/root/.ssh': '/root/.ssh']).arguments(tunnel, 'root@localhost').build())
        outputSink.out(commandId, "Started autossh tunnel for service ${serviceName}.")
    }

}