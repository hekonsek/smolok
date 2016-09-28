package net.smolok.lib.spark.ml

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession

import static TrainingFeatureVector.matching
import static TrainingFeatureVector.notMatching
import static net.smolok.lib.spark.ml.IdentifiableFeatureVector.identifiableVector

class PredictionEngine {

    private final SparkSession spark

    private final def logisticRegression = new LogisticRegression()

    private LogisticRegressionModel model

    PredictionEngine(SparkSession spark) {
        this.spark = spark
    }

    void train(List<TrainingFeatureVector> trainingData) {
        def dataFrame = spark.createDataFrame(trainingData.collect{ new LabeledPoint(it.targetFeature ? 1 : 0, Vectors.dense(it.featureVector)) }, LabeledPoint.class)
        model = logisticRegression.train(dataFrame)
    }

    List<Prediction> predict(List<IdentifiableFeatureVector> featureVectors) {
        Dataset<Row> test = spark.createDataFrame(featureVectors.collect{ new LabeledPoint(1, Vectors.dense(it.featureVector)) }, LabeledPoint.class);
        Dataset<Row> results = model.transform(test);
        Dataset<Row> rows = results.select("features", "prediction", "probability")
        rows.collectAsList().collect {
            def sourceVector = ((DenseVector) it.get(0)).values()
            def id = featureVectors.find{ it.featureVector == sourceVector }.id
            def prediction = (double) it.get(1)
            def confidence = (((DenseVector) it.get(2)).values()[1] * 100d) as int
            new Prediction(id, prediction == 1.0d, confidence)
        }
    }

    public static void main(String[] args) {
        def spark = SparkSession
                .builder()
                .master("local[*]")
                .getOrCreate();

        def classification = new PredictionEngine(spark)
        classification.train([notMatching(5.0, 1.0),
                              notMatching(5, 5),
                              matching(10, 5),
                              matching(30, 1)])
        println classification.predict([identifiableVector('xxx', 9, 1)])
    }

}
