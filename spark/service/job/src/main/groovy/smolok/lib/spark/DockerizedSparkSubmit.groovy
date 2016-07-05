package smolok.lib.spark

import org.apache.commons.lang3.Validate
import smolok.lib.docker.Container
import smolok.lib.docker.Docker

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties

class DockerizedSparkSubmit implements SparkSubmit {

    private final Docker docker

    DockerizedSparkSubmit(Docker docker) {
        this.docker = docker
    }

    @Override
    SparkSubmitResult submit(SparkSubmitCommand command) {
        def smolokVersion = artifactVersionFromDependenciesProperties('smolok', 'smolok-lib-common')
        Validate.isTrue(smolokVersion.present, 'Smolok version cannot be resolved.')

        def path = command.path
        if(!path.startsWith('/')) {
            path = "/var/smolok/spark/jobs/${path}".toString()
        }
        def arguments = [path]

        def joinedOutput = docker.execute(new Container("smolok/spark-submit:${smolokVersion.get()}", null, 'host', ['/var/smolok/spark/jobs': '/var/smolok/spark/jobs'], [:], arguments.toArray(new String[0])))
        def output = joinedOutput.findAll{ !it.matches(/.* (INFO|WARN) .*/) }
        def errors = joinedOutput.findAll{ it.matches(/.* (INFO|WARN) .*/) }
        new SparkSubmitResult(output, errors)
    }

}
