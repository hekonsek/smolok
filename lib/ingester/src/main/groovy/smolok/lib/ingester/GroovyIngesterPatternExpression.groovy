package smolok.lib.ingester

class GroovyIngesterPatternExpression implements IngesterPatternExpression<Object> {

    private final String expression

    private final GroovyShell shell

    GroovyIngesterPatternExpression(String expression) {
        this.expression = expression
        this.shell = new GroovyShell()
    }

    @Override
    Object evaluate() {
        shell.evaluate(expression)
    }

}
