package smolok.paas

/**
 * Point of contact with PaaS implementation capable of starting and managing containers.
 */
interface Paas {

    boolean isProvisioned()

    boolean isStarted()

    /**
     * Starts PaaS platform. Nothing happens is platform is started already.
     */
    void start()

    void stop()

    void reset()

}