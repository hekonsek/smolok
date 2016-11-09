package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommand
import net.smolok.cmd.core.OutputSink
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.Validate
import smolok.lib.docker.ContainerBuilder
import smolok.lib.docker.Docker

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties
import static smolok.lib.docker.ServiceStartupResults.*

class ZeppelinStartCommand extends BaseCommand {

    // Collaborators

    private Docker docker

    // Constructors

    ZeppelinStartCommand(Docker docker) {
        super(['zeppelin', 'start'] as String[])
        this.docker = docker
    }

    // Command operations

    private Optional<String> submitOptions(String... inputCommand) {
        inputCommand = removeOption(inputCommand, 'master')
        inputCommand = removeOption(inputCommand, 'localIP')
        inputCommand = removeOption(inputCommand, 'port')
        inputCommand = removeOption(inputCommand, 'confDir')
        inputCommand = removeOption(inputCommand, 'notebookDir')
        if (inputCommand.length > 2) {
            Optional.of("\"${inputCommand[2..inputCommand.length - 1].join(' ')}\"" as String)
        } else {
            Optional.empty()
        }
    }

    @Override
    void handle(OutputSink outputSink, String commandId, String... inputCommand) {
        def smolokVersion = artifactVersionFromDependenciesProperties('net.smolok', 'smolok-paas')
        Validate.isTrue(smolokVersion.present, 'Smolok version cannot be resolved.')

        def containerBuilder = new ContainerBuilder("smolok/zeppelin:${smolokVersion.get()}").
                name("zeppelin").net('host').
                volumes(['/var/smolok/spark': '/var/smolok/spark', '/var/smolok/zeppelin': '/var/smolok/zeppelin'])

        def defaultMaster = {
            if (StringUtils.containsIgnoreCase(inputCommand.join(' '), '--deploy-mode cluster')) {
                'spark://localhost:6066'
            } else {
                'spark://localhost:7077'
            }
        }

        def environment = [:]
        environment['MASTER'] = option(inputCommand, 'master', defaultMaster())
        environment['SPARK_HOME'] = '/opt/spark'

        def localIP = option(inputCommand, 'localIP')
        if (localIP.isPresent()) {
            environment['SPARK_LOCAL_IP'] = localIP.get()
        }

        environment['ZEPPELIN_PORT'] = option(inputCommand, 'port', '8080')
        environment['ZEPPELIN_CONF_DIR'] = option(inputCommand, 'confDir', '/opt/zeppelin/conf')
        environment['ZEPPELIN_NOTEBOOK_DIR'] = option(inputCommand, 'notebookDir', '/opt/zeppelin/notebook')

        def submitOptions = submitOptions(inputCommand)
        if (submitOptions.isPresent()) {
            environment['SPARK_SUBMIT_OPTIONS'] = submitOptions.get()
        }

        containerBuilder.environment(environment)
        def container = containerBuilder.build()
        switch (docker.startService(container)) {
            case alreadyRunning:
                outputSink.out(commandId, "Zeppelin is already running. No need to start it.")
                break
            case started:
                outputSink.out(commandId, "Started existing Zeppelin instance.")
                break
            case created:
                outputSink.out(commandId, "No Zeppelin found. New one created and started.")
                break
        }

    }
}
