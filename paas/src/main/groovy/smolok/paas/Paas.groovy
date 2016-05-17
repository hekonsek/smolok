package smolok.paas

/**
 * Point of contact with PaaS implementation capable of starting and managing containers.
 */
interface Paas {

    boolean isProvisioned()

    boolean isStarted()

    /**
     * Starts PaaS platform and event bus on the top of it. Before the method call ends, both PaaS and event bus must be
     * up and running.
     *
     * Nothing happens if this method is called while platform bus is started already.
     */
    void start()

    void stop()

    void reset()

}