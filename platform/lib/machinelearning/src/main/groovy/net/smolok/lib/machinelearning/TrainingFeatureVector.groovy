package net.smolok.lib.machinelearning

class TrainingFeatureVector extends FeatureVector {

    boolean targetFeature

    TrainingFeatureVector(double[] featureVector, boolean targetFeature) {
        super(featureVector)
        this.targetFeature = targetFeature
    }

    TrainingFeatureVector() {
    }

    static TrainingFeatureVector matching(double[] featureVector) {
        new TrainingFeatureVector(featureVector, true)
    }

    static TrainingFeatureVector notMatching(double[] featureVector) {
        new TrainingFeatureVector(featureVector, false)
    }

    boolean getTargetFeature() {
        return targetFeature
    }

    void setTargetFeature(boolean targetFeature) {
        this.targetFeature = targetFeature
    }

}
