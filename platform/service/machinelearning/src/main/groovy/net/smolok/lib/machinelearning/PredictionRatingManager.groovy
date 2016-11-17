package net.smolok.lib.machinelearning

interface PredictionRatingManager {

    void rate(String predictionName, String configurationName, String vectorId, boolean rate)

    int rating(String predictionName, String configurationName)

}
