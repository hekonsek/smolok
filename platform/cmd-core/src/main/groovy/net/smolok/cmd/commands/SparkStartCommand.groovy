package net.smolok.cmd.commands

import net.smolok.cmd.core.OutputSink
import org.apache.commons.lang3.Validate
import org.slf4j.LoggerFactory
import net.smolok.cmd.core.BaseCommand

import smolok.lib.docker.ContainerBuilder
import smolok.lib.docker.ServiceStartupResults
import smolok.lib.docker.Docker

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties
import static ServiceStartupResults.alreadyRunning
import static ServiceStartupResults.created
import static ServiceStartupResults.started

/**
 * Starts Spark cluster consisting of single master and slave nodes.
 */
class SparkStartCommand extends BaseCommand {

    private static final LOG = LoggerFactory.getLogger(SparkStartCommand.class)

    // Collaborators

    private final Docker docker

    // Constructors

    SparkStartCommand(Docker docker) {
        super(['spark', 'start'] as String[])
        this.docker = docker
    }

    // Command operations

    @Override
    void handle(OutputSink outputSink, String commandId, String... inputCommand) {
        def smolokVersion = artifactVersionFromDependenciesProperties('net.smolok', 'smolok-paas')
        Validate.isTrue(smolokVersion.present, 'Smolok version cannot be resolved.')

        def masterUrl = option(inputCommand, 'master')

        def host = option(inputCommand, 'host', 'localhost')

        def masterIP = option(inputCommand, 'masterIP')

        def localIP = option(inputCommand, 'localIP')

        if(inputCommand.length < 3) {
            LOG.debug('No node type specified - starting master and worker nodes...')
            startSparkNode(outputSink, commandId, smolokVersion.get(), 'master', masterUrl, host, masterIP, localIP)
            startSparkNode(outputSink, commandId, smolokVersion.get(), 'worker', masterUrl, host, masterIP, localIP)
        } else if(inputCommand[2] == 'master') {
            startSparkNode(outputSink, commandId, smolokVersion.get(), 'master', masterUrl, host, masterIP, localIP)
        } else if(inputCommand[2] == 'worker') {
            startSparkNode(outputSink, commandId, smolokVersion.get(), 'worker', masterUrl, host, masterIP, localIP)
        } else {
            throw new RuntimeException("Unknown Spark node type: ${inputCommand[2]}")
        }
    }

    // Private helpers

    private void startSparkNode(OutputSink outputSink, String commandId, String imageVersion, String nodeType, Optional<String> masterUrl, String host, Optional<String> masterIP, Optional<String> localIP) {
        LOG.debug('Starting Spark node: {}', nodeType)
        switch(new SparkClusterManager(docker).startSparkNode(imageVersion, nodeType, masterUrl, host, masterIP.orElse(null), localIP.orElse(null))) {
            case alreadyRunning:
                outputSink.out(commandId, "Spark ${nodeType} is already running. No need to start it.")
                break
            case started:
                outputSink.out(commandId, "Started existing Spark ${nodeType} instance.")
                break
            case created:
                outputSink.out(commandId, "No Spark ${nodeType} found. New one created and started.")
                break
        }
    }

    static class SparkClusterManager {

        private final Docker docker

        SparkClusterManager(Docker docker) {
            this.docker = docker
        }

        ServiceStartupResults startSparkNode(String imageVersion, String nodeType, Optional<String> masterUrl, String host, String masterIP, String localIP) {
            LOG.debug('Starting Spark node: {}', nodeType)
            def containerBuilder = new ContainerBuilder("smolok/spark-standalone-${nodeType}:${imageVersion}").
                    name("spark-${nodeType}").net('host').
                    volumes(['/var/smolok/spark': '/var/smolok/spark'])
            def environment = [:]
            if(masterUrl.present) {
                environment['SPARK_MASTER'] = masterUrl.get()
            }
            if(host != null) {
                environment['HOST'] = host
            }
            if(masterIP != null) {
                environment['SPARK_MASTER_IP'] = masterIP
            }
            if(localIP != null) {
                environment['SPARK_LOCAL_IP'] = localIP
            }
            containerBuilder.environment(environment)
            def container = containerBuilder.build()
            docker.startService(container)
        }

    }

}