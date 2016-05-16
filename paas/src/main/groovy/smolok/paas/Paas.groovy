package smolok.paas

/**
 * Point of contact with PaaS implementation capable of starting and managing containers.
 */
interface Paas {

    boolean isProvisioned()

    boolean isStarted()

    void start()

    void stop()

    void reset()

}