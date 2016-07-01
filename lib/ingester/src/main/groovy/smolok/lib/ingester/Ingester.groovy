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
           if(it.value.toString().startsWith('RANDOM_STRING')) {
               def randomSet = it.value.toString()
               randomSet = randomSet.replaceFirst(/RANDOM_STRING\(/, '')
               randomSet = randomSet.replaceFirst(/\)/, '')
               columns[it.key] = new Random().nextInt(randomSet.toInteger()) + ''
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
