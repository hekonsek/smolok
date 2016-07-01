package smolok.lib.docker

/**
 * Higher-level service around Docker client. Used to invoke common Docker operations like run-once command execution
 * or running named daemon services.
 */
interface Docker {

    List<String> execute(Container container)

    ServiceStartupStatus startService(Container container)

    ContainerStatus status(String name)

}