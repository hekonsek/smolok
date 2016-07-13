package smolok.lib.ingester

class RandomStringIngesterPatternExpression implements IngesterPatternExpression<String> {

    private static final RANDOM = new Random()

    private final int range

    RandomStringIngesterPatternExpression(int range) {
        this.range = range
    }

    @Override
    String evaluate() {
        "${RANDOM.nextInt(range)}"
    }

}
