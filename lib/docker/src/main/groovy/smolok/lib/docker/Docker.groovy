package smolok.lib.docker

import smolok.lib.process.ProcessManager

import static smolok.lib.docker.Docker.ContainerStartupStatus.alreadyRunning
import static smolok.lib.docker.Docker.ContainerStartupStatus.created
import static smolok.lib.docker.Docker.ContainerStartupStatus.started
import static smolok.lib.process.ExecutorBasedProcessManager.command

class Docker {

    private final ProcessManager processManager

    Docker(ProcessManager processManager) {
        this.processManager = processManager
    }

    ContainerStartupStatus createAndStart(Container container) {
        if(processManager.execute(command("docker ps -a -f name=${container.name()}")).size() > 1) {
            if(processManager.execute(command("docker ps -f name=${container.name()}")).size() == 1) {
                processManager.execute(command("docker start ${container.name()}"))
                return started
            } else {
                return alreadyRunning
            }
        } else {
            processManager.execute(command("docker run -d --net=${container.net()} --name ${container.name()} -t ${container.image()}"))
            return created
        }
    }

    static enum ContainerStartupStatus {
        created, started, alreadyRunning
    }

}
