package smolok.lib.docker

/**
 * Higher-level service around Docker client. Used to invoke common Docker operations like run-once command execution
 * or running named daemon services.
 */
interface Docker {

    /**
     * Starts defined container and returns output of the command. Used for executing and parsing short-living
     * dockerized commands.
     *
     * @param container container definition.
     * @return output of the container execution, one line per list element.
     */
    List<String> execute(Container container)

    ServiceStartupStatus startService(Container container)

    ContainerStatus status(String name)

    void stop(String name)

}