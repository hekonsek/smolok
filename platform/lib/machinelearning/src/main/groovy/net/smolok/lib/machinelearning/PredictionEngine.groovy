package net.smolok.lib.machinelearning

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

import static org.slf4j.LoggerFactory.getLogger

class PredictionEngine {

    private static final LOG = getLogger(PredictionEngine.class)

    private final SparkSession spark

    private final def logisticRegression = new LogisticRegression()

    private LogisticRegressionModel model

    PredictionEngine(SparkSession spark) {
        this.spark = spark
    }

    void train(List<TrainingFeatureVector> trainingData) {
        LOG.debug('About to train model using training data: {}', trainingData)

        def vectors = trainingData.collect { new LabeledPoint(it.targetFeature ? 1 : 0, Vectors.dense(it.featureVector)) }
        def dataFrame = spark.createDataFrame(vectors, LabeledPoint.class)
        model = logisticRegression.train(dataFrame)
    }

    List<Prediction> predict(List<IdentifiableFeatureVector> featureVectors) {
        def test = spark.createDataFrame(featureVectors.collect{ new LabeledPoint(1, Vectors.dense(it.featureVector)) }, LabeledPoint.class);
        def results = model.transform(test)
        def rows = results.select("features", "prediction", "probability")
        rows.collectAsList().collect {
            def sourceVector = ((DenseVector) it.get(0)).values()
            def id = featureVectors.find{ it.featureVector == sourceVector }.id
            def prediction = (double) it.get(1)
            def confidence = (((DenseVector) it.get(2)).values()[1] * 100d) as int
            new Prediction(id, prediction == 1.0d, confidence)
        }
    }

}
