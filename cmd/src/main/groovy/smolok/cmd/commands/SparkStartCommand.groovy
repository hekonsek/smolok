package smolok.cmd.commands

import org.apache.commons.lang3.Validate
import smolok.cmd.Command
import smolok.cmd.OutputSink
import smolok.lib.docker.Container
import smolok.lib.docker.ContainerStartupStatus
import smolok.lib.docker.Docker

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties

/**
 * Starts Spark cluster consisting of single master and slave nodes.
 */
class SparkStartCommand implements Command {

    // Collaborators

    private final Docker docker

    // Constructors
    SparkStartCommand(Docker docker) {
        this.docker = docker
    }

    // Command operations

    @Override
    boolean supports(String... command) {
        command[0] == 'spark' && command[1] == 'start'
    }

    @Override
    void handle(OutputSink outputSink, String... inputCommand) {
        def smolokVersion = artifactVersionFromDependenciesProperties('smolok', 'smolok-paas')
        Validate.isTrue(smolokVersion.present, 'Smolok version cannot be resolved.')

        startSparkNode(outputSink, smolokVersion.get(), 'master')
        startSparkNode(outputSink, smolokVersion.get(), 'worker')
    }

    private void startSparkNode(OutputSink outputSink, String smolokVersion, String nodeType) {
        switch(docker.createAndStart(new Container("smolok/spark-standalone-${nodeType}:${smolokVersion}", "spark-${nodeType}", 'host', [:]))) {
            case ContainerStartupStatus.alreadyRunning:
                outputSink.out("Spark ${nodeType} is already running. No need to start it.")
                break
            case ContainerStartupStatus.started:
                outputSink.out("Started existing Spark ${nodeType} instance.")
                break
            case ContainerStartupStatus.created:
                outputSink.out("No Spark ${nodeType} found. New one created and started.")
                break
        }
    }

}