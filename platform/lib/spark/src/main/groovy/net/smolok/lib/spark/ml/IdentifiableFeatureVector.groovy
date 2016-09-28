package net.smolok.lib.spark.ml

class IdentifiableFeatureVector extends FeatureVector {

    String id

    IdentifiableFeatureVector(double[] featureVector, String id) {
        super(featureVector)
        this.id = id
    }

    IdentifiableFeatureVector() {
    }

    static identifiableVector(String id, double... features) {
        new IdentifiableFeatureVector(features, id)
    }

    String getId() {
        return id
    }

    void setId(String id) {
        this.id = id
    }

}
