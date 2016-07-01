package smolok.cmd.commands

import org.apache.commons.lang3.Validate
import org.slf4j.LoggerFactory
import smolok.cmd.Command
import smolok.cmd.OutputSink
import smolok.lib.docker.ContainerBuilder
import smolok.lib.docker.ServiceStartupStatus
import smolok.lib.docker.Docker

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties
import static ServiceStartupStatus.alreadyRunning
import static ServiceStartupStatus.created
import static ServiceStartupStatus.started

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

        def masterUrl = inputCommand.find{ it.startsWith('--master=') }
        if(masterUrl != null) {
            masterUrl = masterUrl.replaceFirst(/--master=/, '')
        }

        if(inputCommand.length < 3) {
            LOG.debug('No node type specified - starting master and worker nodes...')
            startSparkNode(outputSink, smolokVersion.get(), 'master', masterUrl)
            startSparkNode(outputSink, smolokVersion.get(), 'worker', masterUrl)
        } else if(inputCommand[2] == 'master') {
            startSparkNode(outputSink, smolokVersion.get(), 'master', masterUrl)
        } else if(inputCommand[2] == 'worker') {
            startSparkNode(outputSink, smolokVersion.get(), 'worker', masterUrl)
        } else {
            throw new RuntimeException("Unknown Spark node type: ${inputCommand[2]}")
        }
    }

    // Private helpers

    private void startSparkNode(OutputSink outputSink, String imageVersion, String nodeType, String masterUrl) {
        LOG.debug('Starting Spark node: {}', nodeType)
        switch(new SparkClusterManager(docker).startSparkNode(imageVersion, nodeType, masterUrl)) {
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

    static class SparkClusterManager {

        private final Docker docker

        SparkClusterManager(Docker docker) {
            this.docker = docker
        }

        ServiceStartupStatus startSparkNode(String imageVersion, String nodeType, String masterUrl) {
            LOG.debug('Starting Spark node: {}', nodeType)
            def containerBuilder = new ContainerBuilder("smolok/spark-standalone-${nodeType}:${imageVersion}").
                    name("spark-${nodeType}").net('host').
                    volumes(['/var/smolok/spark/jobs': '/var/smolok/spark/jobs'])
            if(masterUrl != null) {
                containerBuilder.environment([SPARK_MASTER: masterUrl])
            }
            def container = containerBuilder.build()
            docker.startService(container)
        }

    }

}