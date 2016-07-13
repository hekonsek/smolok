package smolok.lib.ingester

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableMap

class IngesterPatternParser {

    Map<String, Object> parsePrototype(String pattern) {
        def prototype = new ObjectMapper().readValue(pattern, Map.class)
        def columns = prototype.values().first() as Map<String, Object>
        columns.entrySet().each {
            if(it.value.toString().startsWith('randomString')) {
                columns[it.key] = new RandomStringIngesterPatternExpression(parseArguments(it.value.toString())[0].toInteger())
            } else if(it.value.toString().startsWith('groovy')) {
                columns[it.key] = new GroovyIngesterPatternExpression(parseArguments(it.value.toString())[0])
            }
        }
        ImmutableMap.copyOf(prototype)
    }

    private List<String> parseArguments(String function) {
        def functionWithoutName = function.substring(function.indexOf('('))
        def functionWithoutBracets = functionWithoutName.substring(1, functionWithoutName.length() - 1)
        functionWithoutBracets.split(',').collect{ it.trim() }
    }

}
