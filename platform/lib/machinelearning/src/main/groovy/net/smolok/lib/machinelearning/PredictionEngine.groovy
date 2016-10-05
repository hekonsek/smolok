package net.smolok.lib.machinelearning

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.classification.OneVsRest
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

import static org.slf4j.LoggerFactory.getLogger

class PredictionEngine {

    private static final LOG = getLogger(PredictionEngine.class)

    private final SparkSession spark

    private PipelineModel model

    boolean multiclass

    PredictionEngine(SparkSession spark) {
        this.spark = spark
    }

    void train(String algorithm, List<TrainingFeatureVector> trainingData) {
        LOG.debug('About to train {} model using training data: {}', algorithm, trainingData)

        def vectors = trainingData.collect {
            def targetFeature = it.targetFeature
            if (targetFeature instanceof Boolean) {
                targetFeature = targetFeature ? 1 : 0
            }
            new LabeledPoint((double) targetFeature, Vectors.dense(it.featureVector()))
        }
        def dataFrame = spark.createDataFrame(vectors, LabeledPoint.class)

        multiclass = trainingData.first() instanceof MulticlassTrainingFeatureVector
        def algorithmImpl
        if (algorithm == 'NaiveBayes') {
            algorithmImpl = new NaiveBayes()
        } else {
            algorithmImpl = new LogisticRegression()
        }
        if (multiclass) {
            algorithmImpl = new OneVsRest().setClassifier(algorithmImpl)
        }
        model = new Pipeline().setStages(algorithmImpl).fit(dataFrame)
    }

    void train(String algorithm, TrainingFeatureVector... trainingData) {
        train(algorithm, trainingData.toList())
    }

    void train(List<TrainingFeatureVector> trainingData) {
        train('LogisticRegression', trainingData)
    }

    void train(TrainingFeatureVector... trainingData) {
        train(trainingData.toList())
    }

    List<Prediction> predict(List<IdentifiableFeatureVector> featureVectors) {
        def test = spark.createDataFrame(featureVectors.collect {
            new LabeledPoint(1, Vectors.dense(it.featureVector()))
        }, LabeledPoint.class);
        def results = model.transform(test)
        def rows = multiclass ? results.select("features", "prediction") :  results.select("features", "prediction", "probability")

        rows.collectAsList().collect {
            def sourceVector = ((DenseVector) it.get(0)).values()
            def id = featureVectors.find { it.featureVector() == sourceVector }.id
            def prediction = (double) it.get(1)
            def confidence = multiclass ? -1 : (((DenseVector) it.get(2)).values()[1] * 100d) as int
            new Prediction(id, prediction, confidence)
        }
    }

}
