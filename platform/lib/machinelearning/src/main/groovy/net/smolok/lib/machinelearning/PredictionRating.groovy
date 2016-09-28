package net.smolok.lib.machinelearning

class PredictionRating {

    String vectorId

    boolean value

    PredictionRating(String vectorId, boolean value) {
        this.vectorId = vectorId
        this.value = value
    }

    String getVectorId() {
        return vectorId
    }

    boolean getValue() {
        return value
    }

}
