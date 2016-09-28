package net.smolok.lib.spark.ml;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.LabeledPoint;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.ml.linalg.DenseVector;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRow;
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.spark_project.guava.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

class Classification {

    SparkSession spark

    def lr = new LogisticRegression()

    LogisticRegressionModel model

    Classification(SparkSession spark) {
        this.spark = spark
    }

    void train(List<TrainingData> trainingData) {
        def dataFrame = spark.createDataFrame(trainingData.collect{ new LabeledPoint(it.targetFeature ? 1 : 0, Vectors.dense(it.featureVector)) }, LabeledPoint.class)
        model = lr.train(dataFrame)
    }

    public static void main(String[] args) {
        def spark = SparkSession
                .builder()
                .master("local[*]")
                .getOrCreate();

        def classification = new Classification(spark)
        classification.train([new TrainingData([5.0, 1.0], false),
                              new TrainingData([5, 5], false),
                              new TrainingData([10, 5], true),
                              new TrainingData([30, 1], true)])

        Dataset<Row> test = spark.createDataFrame(Arrays.asList(
                new LabeledPoint(1.0, Vectors.dense(9, 1))
        ), LabeledPoint.class);

        Dataset<Row> results = classification.model.transform(test);
        Dataset<Row> rows = results.select("features", "label", "probability", "prediction");
        for (Row r : rows.collectAsList()) {
            System.out.println("(" + r.get(0) + ", " + r.get(1) + ") -> prob=" + r.get(2)
                    + ", prediction=" + r.get(3));
        }
    }

}
