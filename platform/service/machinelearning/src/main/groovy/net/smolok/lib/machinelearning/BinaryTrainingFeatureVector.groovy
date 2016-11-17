package net.smolok.lib.machinelearning

class BinaryTrainingFeatureVector extends TrainingFeatureVector<Boolean> {

    BinaryTrainingFeatureVector(double[] featureVector, boolean targetFeature) {
        super(targetFeature, featureVector)
    }

    BinaryTrainingFeatureVector() {
    }

    static BinaryTrainingFeatureVector matching(double[] featureVector) {
        new BinaryTrainingFeatureVector(featureVector, true)
    }

    static BinaryTrainingFeatureVector notMatching(double[] featureVector) {
        new BinaryTrainingFeatureVector(featureVector, false)
    }

}
