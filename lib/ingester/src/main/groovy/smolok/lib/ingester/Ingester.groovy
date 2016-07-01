package smolok.lib.ingester

class Ingester {

    private final Map<String, Object> pattern

    Ingester(String pattern) {
        this.pattern = new IngesterPatternParser().parsePrototype(pattern)
    }

    Map<String, Object> nextRecord() {
        def row = new HashMap(pattern)
        def columns = row.values().first() as Map<String, Object>
        columns.entrySet().each {
           if(it.value instanceof IngesterPatternExpression) {
               columns[it.key] = ((IngesterPatternExpression) it.value).evaluate()
           }
        }
        row
    }

    void ingest(IngesterSink sink, int records) {
        (1..records).each {
            sink.consume(nextRecord())
        }
    }

}
