package net.smolok.lib.spark.ml

import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true)
class Prediction {

    private final String vectorId

    private final boolean result

    private final int confidence

    Prediction(String vectorId, boolean result, int confidence) {
        this.vectorId = vectorId
        this.result = result
        this.confidence = confidence
    }

    String vectorId() {
        return vectorId
    }

    boolean result() {
        return result
    }

    int confidence() {
        return confidence
    }

}
