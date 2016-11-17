package net.smolok.cmd.commands

import net.smolok.cmd.spi.OutputSink
import org.apache.commons.lang3.Validate
import net.smolok.cmd.core.BaseCommandHandler

import smolok.lib.docker.Container
import smolok.lib.docker.Docker

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties

class SparkSubmitCommandHandler extends BaseCommandHandler {

    // Collaborators

    private final Docker docker

    // Constructors
    SparkSubmitCommandHandler(Docker docker) {
        super('spark', 'submit')
        this.docker = docker
    }

    // CommandHandler operations

    @Override
    void handle(OutputSink outputSink, String commandId, String... inputCommand) {
        def smolokVersion = artifactVersionFromDependenciesProperties('net.smolok', 'smolok-paas')
        Validate.isTrue(smolokVersion.present, 'Smolok version cannot be resolved.')

        if (!hasOption(inputCommand, 'master')) {
            if (option(inputCommand, 'deploy-mode', '').equalsIgnoreCase('cluster')) {
                inputCommand = putOptionAt(inputCommand, 2, '--master=spark://localhost:6066')
            } else {
                inputCommand = putOptionAt(inputCommand, 2, '--master=spark://localhost:7077')
            }
        }

        def keepLogs = 'keep-logs'
        def cleanUp = !hasOption(inputCommand, keepLogs)
        inputCommand = removeOption(inputCommand, keepLogs)

        def arguments = inputCommand[2..inputCommand.length - 1].toArray(new String[0])
        def indexOfJobArtifact = arguments.findIndexOf { !it.startsWith('-') }
        if(!arguments[indexOfJobArtifact].startsWith('/')) {
            arguments[indexOfJobArtifact] = "/var/smolok/spark/jobs/${arguments[indexOfJobArtifact]}".toString()
        }
        docker.execute(new Container("smolok/spark-submit:${smolokVersion.get()}", null, 'host', cleanUp, ['/var/smolok/spark': '/var/smolok/spark'], [:], arguments)).each {
            outputSink.out(commandId, it)
        }
    }

}