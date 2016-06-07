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

    ContainerStartupStatus createAndStart(Container container) {
        switch(status(container.name())) {
            case running: return alreadyRunning
            case created:
                processManager.execute(command("docker start ${container.name()}"))
                return started
            case none:
                processManager.execute(command("docker run -d --net=${container.net()} --name ${container.name()} -t ${container.image()} ${container.arguments().join(' ')}"))
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

}
