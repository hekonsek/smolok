package smolok.lib.ingester

import com.fasterxml.jackson.databind.ObjectMapper

class IngesterPatternParser {

    Map<String, Object> parsePrototype(String pattern) {
        def prototype = new ObjectMapper().readValue(pattern, Map.class)
        def columns = prototype.values().first() as Map<String, Object>
        columns.entrySet().each {
            if(it.value.toString().startsWith('RANDOM_STRING')) {
                //columns[it.key] = new RandomStringPatternExpression(randomSet.toInteger())
            }
        }
        prototype
    }

}
