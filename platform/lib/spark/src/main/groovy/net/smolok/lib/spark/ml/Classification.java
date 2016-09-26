package net.smolok.lib.spark.ml;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.Serializable;
import java.util.Arrays;

class Classification {

    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("JavaLogisticRegressionSummaryExample")
                .getOrCreate();
        Dataset<Row> training = spark.createDataFrame(Arrays.asList(
                new JavaLabeledDocument(0L, "a b c d e spark", 1.0),
                new JavaLabeledDocument(1L, "b d", 0.0),
                new JavaLabeledDocument(2L, "spark f g h", 1.0),
                new JavaLabeledDocument(9L, "spark i", 1.0),
                new JavaLabeledDocument(3L, "hadoop mapreduce", 0.0)
        ), JavaLabeledDocument.class);

// Configure an ML pipeline, which consists of three stages: tokenizer, hashingTF, and lr.
        Tokenizer tokenizer = new Tokenizer()
                .setInputCol("text")
                .setOutputCol("words");
        HashingTF hashingTF = new HashingTF()
                .setNumFeatures(1000)
                .setInputCol(tokenizer.getOutputCol())
                .setOutputCol("features");
        LogisticRegression lr = new LogisticRegression()
                .setMaxIter(10)
                .setRegParam(0.01);
        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[] {tokenizer, hashingTF, lr});

// Fit the pipeline to training documents.
        PipelineModel model = pipeline.fit(training);

// Prepare test documents, which are unlabeled.
        Dataset<Row> test = spark.createDataFrame(Arrays.asList(
                new JavaDocument(4L, "spark i j k"),
                new JavaDocument(5L, "l m n"),
                new JavaDocument(6L, "mapreduce spark"),
                new JavaDocument(7L, "apache hadoop"),
                new JavaDocument(8L, "spark")
        ), JavaDocument.class);

// Make predictions on test documents.
        Dataset<Row> predictions = model.transform(test);
        for (Row r : predictions.select("id", "text", "probability", "prediction").collectAsList()) {
            System.out.println("(" + r.get(0) + ", " + r.get(1) + ") --> prob=" + r.get(2)
                    + ", prediction=" + r.get(3));
        }
    }

    static public class JavaLabeledDocument extends JavaDocument implements Serializable {

        private double label;

        public JavaLabeledDocument(long id, String text, double label) {
            super(id, text);
            this.label = label;
        }

        public double getLabel() {
            return this.label;
        }
    }

    static public class JavaDocument implements Serializable {

        private long id;
        private String text;

        public JavaDocument(long id, String text) {
            this.id = id;
            this.text = text;
        }

        public long getId() {
            return this.id;
        }

        public String getText() {
            return this.text;
        }
    }

}
