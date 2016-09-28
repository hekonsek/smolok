package net.smolok.lib.spark.ml

class TrainingData {

    double[] featureVector

    boolean targetFeature

    TrainingData(List<Double> featureVector, boolean targetFeature) {
        this.featureVector = featureVector.toArray() as double[]
        this.targetFeature = targetFeature
    }

    TrainingData(double[] featureVector, boolean targetFeature) {
        this.featureVector = featureVector
        this.targetFeature = targetFeature
    }

    TrainingData() {
    }

    double[] getFeatureVector() {
        return featureVector
    }

    void setFeatureVector(double[] featureVector) {
        this.featureVector = featureVector
    }

    boolean getTargetFeature() {
        return targetFeature
    }

    void setTargetFeature(boolean targetFeature) {
        this.targetFeature = targetFeature
    }

}
