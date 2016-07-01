package smolok.lib.ingester

interface IngesterSink {

    void consume(Map<String, Object> record)

}