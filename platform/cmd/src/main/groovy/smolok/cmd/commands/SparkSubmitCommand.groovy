package smolok.cmd.commands

import org.apache.commons.lang3.Validate
import smolok.cmd.Command
import smolok.cmd.OutputSink
import smolok.lib.docker.Container
import smolok.lib.docker.Docker

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties

class SparkSubmitCommand implements Command {

    // Collaborators

    private final Docker docker

    // Constructors
    SparkSubmitCommand(Docker docker) {
        this.docker = docker
    }

    // Command operations

    @Override
    boolean supports(String... command) {
        command[0] == 'spark' && command[1] == 'submit'
    }

    @Override
    void handle(OutputSink outputSink, String... inputCommand) {
        def smolokVersion = artifactVersionFromDependenciesProperties('net.smolok', 'smolok-paas')
        Validate.isTrue(smolokVersion.present, 'Smolok version cannot be resolved.')

        def arguments = inputCommand[2..inputCommand.length - 1].toArray(new String[0])
        if(!arguments.last().startsWith('/')) {
            arguments[arguments.length - 1] = "/var/smolok/spark/jobs/${arguments.last()}".toString()
        }
        docker.execute(new Container("smolok/spark-submit:${smolokVersion.get()}", null, 'host', ['/var/smolok/spark/jobs': '/var/smolok/spark/jobs'], [:], arguments)).each {
            outputSink.out(it)
        }
    }

}