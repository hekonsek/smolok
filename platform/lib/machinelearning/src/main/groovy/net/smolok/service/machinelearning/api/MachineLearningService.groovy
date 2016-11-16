package net.smolok.service.machinelearning.api

interface MachineLearningService {

    void storeTrainingData(String collection, FeatureVector featureVector)

    Map<String, Double> predict(String collection, FeatureVector featureVector)

}