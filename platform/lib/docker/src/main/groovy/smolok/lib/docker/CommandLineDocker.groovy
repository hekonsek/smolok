package smolok.lib.docker

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.Validate
import smolok.lib.process.ProcessManager

import static org.slf4j.LoggerFactory.getLogger
import static ServiceStartupResults.alreadyRunning
import static ServiceStartupResults.started
import static smolok.lib.docker.ContainerStatus.created
import static smolok.lib.docker.ContainerStatus.none
import static smolok.lib.docker.ContainerStatus.running
import static smolok.lib.process.ExecutorBasedProcessManager.command

class CommandLineDocker implements Docker {

    private final static LOG = getLogger(CommandLineDocker.class)

    private final static MAPPER = new ObjectMapper()

    private final ProcessManager processManager

    // Constructor

    CommandLineDocker(ProcessManager processManager) {
        this.processManager = processManager
    }

    // Operations implementation

    @Override
    List<String> execute(Container container) {
        processManager.execute(command(buildRunCommand(container, false)))
    }

    ServiceStartupResults startService(Container container) {
        Validate.notNull(container, 'Container cannot be null.')
        LOG.debug('About to execute container service: {}', container)

        Validate.notBlank(container.name(), 'Container service name must not be empty.')

        switch(status(container.name())) {
            case running: return alreadyRunning
            case created:
                processManager.execute(command("docker start ${container.name()}"))
                return started
            case none:
                processManager.execute(command(buildRunCommand(container, true)))
                return ServiceStartupResults.created
        }
    }

    ContainerStatus status(String name) {
        if (processManager.execute(command("docker ps -a -f name=${name}")).size() > 1) {
            if (processManager.execute(command("docker ps -f name=${name}")).size() > 1) {
                running
            } else {
                created
            }
        } else {
            none
        }
    }

    @Override
    void stop(String name) {
        Validate.notBlank(name, 'Container name cannot be blank.')
        LOG.debug('About to stop container: {}', name)

        processManager.execute(command("docker stop ${name}"))
    }

    @Override
    InspectResults inspect(String containerId) {
        def commandOutput = processManager.execute(command("docker inspect ${containerId}")).join(' ')
        def trimmedCommandOutput = commandOutput.substring(1, commandOutput.length() - 1)
        new InspectResults(MAPPER.readValue(trimmedCommandOutput, Map.class))
    }

    // Helpers

    static private String buildRunCommand(Container container, boolean daemon) {
        def command = 'docker run'
        if(daemon) {
            command += ' -d'
        }
        if(container.name() != null) {
            command += " --name=${container.name()}"
        }
        if(container.net() != null) {
            command += " --net=${container.net()} "
        }
        command += " ${container.volumes().inject('') { volumes, volume -> "${volumes} -v ${volume.key}:${volume.value}"}}"
        command += " ${container.environment().inject('') { environment, variable -> "${environment} -e ${variable.key}=${variable.value}"}}"
        command + " -t ${container.image()} ${container.arguments().join(' ')}"
    }

}
