package net.smolok.lib.machinelearning

class MulticlassTrainingFeatureVector extends TrainingFeatureVector<Double> {

    MulticlassTrainingFeatureVector(Double targetFeature, double ... featureVector) {
        super(targetFeature, featureVector)
    }

    MulticlassTrainingFeatureVector() {
    }

    static MulticlassTrainingFeatureVector trainingFeatureVector(double targetFeature, double... featureVector) {
        new MulticlassTrainingFeatureVector(targetFeature, featureVector)
    }

}
