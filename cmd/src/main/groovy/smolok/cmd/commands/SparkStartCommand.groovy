package smolok.cmd.commands

import org.apache.commons.lang3.Validate
import smolok.cmd.Command
import smolok.cmd.OutputSink
import smolok.lib.docker.Container
import smolok.lib.docker.ContainerStartupStatus
import smolok.lib.docker.Docker
import smolok.lib.process.ProcessManager

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties
import static smolok.lib.process.ExecutorBasedProcessManager.command

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

        switch(docker.createAndStart(new Container("smolok/spark-standalone-master:${smolokVersion.get()}", 'spark-master', 'host'))) {
            case ContainerStartupStatus.alreadyRunning:
                outputSink.out('Spark master is already running. No need to start it.')
                break
            case ContainerStartupStatus.started:
                outputSink.out('Started existing Spark master instance.')
                break
            case ContainerStartupStatus.created:
                outputSink.out('No Spark master found. New one created and started.')
                break
        }

        switch(docker.createAndStart(new Container("smolok/spark-standalone-worker:${smolokVersion.get()}", 'spark-worker', 'host'))) {
            case ContainerStartupStatus.alreadyRunning:
                outputSink.out('Spark worker is already running. No need to start it.')
                break
            case ContainerStartupStatus.started:
                outputSink.out('Started existing Spark worker instance.')
                break
            case ContainerStartupStatus.created:
                outputSink.out('No Spark worker found. New one created and started.')
                break
        }
    }

}