package smolok.lib.docker

interface Docker {

    ContainerStartupStatus createAndStart(Container container)

    ContainerStatus status(String name);

}