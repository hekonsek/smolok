package net.smolok.lib.machinelearning

class FeatureVector {

    double[] featureVector

    FeatureVector(double... featureVector) {
        this.featureVector = featureVector
    }

    FeatureVector() {
    }

    double[] getFeatureVector() {
        return featureVector
    }

    void setFeatureVector(double[] featureVector) {
        this.featureVector = featureVector
    }

}
