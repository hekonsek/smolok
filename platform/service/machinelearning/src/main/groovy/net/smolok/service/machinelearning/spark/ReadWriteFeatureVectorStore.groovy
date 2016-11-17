package net.smolok.service.machinelearning.spark

import net.smolok.service.machinelearning.api.FeatureVector

interface ReadWriteFeatureVectorStore extends FeatureVectorStore {

    void write(String collection, FeatureVector featureVector)

    List<FeatureVector> read(String collection)

}