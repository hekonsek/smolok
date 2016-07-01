package smolok.lib.ingester

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableMap

class IngesterPatternParser {

    Map<String, Object> parsePrototype(String pattern) {
        def prototype = new ObjectMapper().readValue(pattern, Map.class)
        def columns = prototype.values().first() as Map<String, Object>
        columns.entrySet().each {
            if(it.value.toString().startsWith('randomString')) {
                def randomSet = it.value.toString()
                randomSet = randomSet.replaceFirst(/randomString\(/, '')
                randomSet = randomSet.replaceFirst(/\)/, '')
                columns[it.key] = new RandomStringIngesterPatternExpression(randomSet.toInteger())
            } else if(it.value.toString().startsWith('groovy')) {
                def expression = it.value.toString()
                expression = expression.replaceFirst(/groovy\(/, '')
                expression = expression.replaceFirst(/\)/, '')
                columns[it.key] = new GroovyIngesterPatternExpression(expression)
            }
        }
        ImmutableMap.copyOf(prototype)
    }

}
