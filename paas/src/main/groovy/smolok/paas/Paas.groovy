package smolok.paas

interface Paas {

    boolean isProvisioned()

    boolean isStarted()

    void start()

    void stop()

    void reset()

}