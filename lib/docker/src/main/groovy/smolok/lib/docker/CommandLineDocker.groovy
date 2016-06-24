package smolok.lib.docker

import smolok.lib.process.ProcessManager

import static smolok.lib.docker.ContainerStartupStatus.alreadyRunning
import static smolok.lib.docker.ContainerStartupStatus.started
import static smolok.lib.docker.ContainerStatus.created
import static smolok.lib.docker.ContainerStatus.none
import static smolok.lib.docker.ContainerStatus.running
import static smolok.lib.process.ExecutorBasedProcessManager.command

class CommandLineDocker implements Docker {

    private final ProcessManager processManager

    CommandLineDocker(ProcessManager processManager) {
        this.processManager = processManager
    }

    @Override
    List<String> execute(Container container) {
        processManager.execute(command(buildRunCommand(container, false)))
    }

    ContainerStartupStatus createAndStart(Container container) {
        switch(status(container.name())) {
            case running: return alreadyRunning
            case created:
                processManager.execute(command("docker start ${container.name()}"))
                return started
            case none:
                processManager.execute(command(buildRunCommand(container, true)))
                return ContainerStartupStatus.created
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
