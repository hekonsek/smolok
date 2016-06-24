package smolok.cmd.commands

import org.apache.commons.lang3.Validate
import org.slf4j.LoggerFactory
import smolok.cmd.Command
import smolok.cmd.OutputSink
import smolok.lib.docker.Container
import smolok.lib.docker.ContainerStartupStatus
import smolok.lib.docker.Docker

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties
import static smolok.lib.docker.ContainerStartupStatus.alreadyRunning
import static smolok.lib.docker.ContainerStartupStatus.created
import static smolok.lib.docker.ContainerStartupStatus.started

/**
 * Starts Spark cluster consisting of single master and slave nodes.
 */
class SparkStartCommand implements Command {

    private static final LOG = LoggerFactory.getLogger(SparkStartCommand.class)

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

        if(inputCommand.length < 3) {
            LOG.debug('No node type specified - starting master and worker nodes...')
            startSparkNode(outputSink, smolokVersion.get(), 'master')
            startSparkNode(outputSink, smolokVersion.get(), 'worker')
        } else if(inputCommand[2] == 'master') {
            startSparkNode(outputSink, smolokVersion.get(), 'master')
        } else if(inputCommand[2] == 'worker') {
            startSparkNode(outputSink, smolokVersion.get(), 'worker')
        } else {
            throw new RuntimeException("Unknown Spark node type: ${inputCommand[2]}")
        }
    }

    // Private helpers

    private void startSparkNode(OutputSink outputSink, String imageVersion, String nodeType) {
        LOG.debug('Starting Spark node: {}', nodeType)
        switch(new SparkNodeManager(docker).startSparkNode(imageVersion, nodeType)) {
            case alreadyRunning:
                outputSink.out("Spark ${nodeType} is already running. No need to start it.")
                break
            case started:
                outputSink.out("Started existing Spark ${nodeType} instance.")
                break
            case created:
                outputSink.out("No Spark ${nodeType} found. New one created and started.")
                break
        }
    }

    static class SparkNodeManager {

        private final Docker docker

        SparkNodeManager(Docker docker) {
            this.docker = docker
        }

        ContainerStartupStatus startSparkNode(String imageVersion, String nodeType) {
            LOG.debug('Starting Spark node: {}', nodeType)
            def container = new Container("smolok/spark-standalone-${nodeType}:${imageVersion}", "spark-${nodeType}",
                    'host', ['/var/smolok/spark/jobs': '/var/smolok/spark/jobs'])
            docker.createAndStart(container)
        }

    }

}