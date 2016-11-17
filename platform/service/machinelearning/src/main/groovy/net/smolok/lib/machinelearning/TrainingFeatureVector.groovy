package net.smolok.lib.machinelearning

abstract class TrainingFeatureVector<T> extends FeatureVector {

    T targetFeature

    TrainingFeatureVector(T targetFeature, double ... featureVector) {
        super(featureVector)
        this.targetFeature = targetFeature
    }

    TrainingFeatureVector() {
    }

    T getTargetFeature() {
        return targetFeature
    }

    void setTargetFeature(T targetFeature) {
        this.targetFeature = targetFeature
    }

}
