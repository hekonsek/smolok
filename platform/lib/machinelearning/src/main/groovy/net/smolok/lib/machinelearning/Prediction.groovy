package net.smolok.lib.machinelearning

import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true)
class Prediction {

    private final String vectorId

    private final double result

    private final int confidence

    Prediction(String vectorId, double result, int confidence) {
        this.vectorId = vectorId
        this.result = result
        this.confidence = confidence
    }

    String id() {
        return vectorId
    }

    double result() {
        result
    }

    boolean binaryResult() {
        result != 0.0d
    }

    int confidence() {
        return confidence
    }

}
