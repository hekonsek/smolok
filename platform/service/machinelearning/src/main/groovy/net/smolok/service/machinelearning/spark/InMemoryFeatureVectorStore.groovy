package net.smolok.service.machinelearning.spark

import net.smolok.service.machinelearning.api.FeatureVector

class InMemoryFeatureVectorStore implements ReadWriteFeatureVectorStore {

    private final Map<String, List<FeatureVector>> featureVectors = [:].withDefault{[]}

    @Override
    void write(String collection, FeatureVector featureVector) {
        featureVectors[collection] << featureVector
    }

    @Override
    List<FeatureVector> read(String collection) {
        featureVectors[collection]
    }

}
