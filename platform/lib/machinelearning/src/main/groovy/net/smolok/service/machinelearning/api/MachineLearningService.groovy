package net.smolok.service.machinelearning.api

interface MachineLearningService {

    void storeTrainingData(String collection, List<FeatureVector> featureVectors)

    Map<String, Double> predict(String collection, FeatureVector featureVector)

}