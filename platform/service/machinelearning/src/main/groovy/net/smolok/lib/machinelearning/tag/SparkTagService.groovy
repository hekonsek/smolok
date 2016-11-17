package net.smolok.lib.machinelearning.tag

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.IDF
import org.apache.spark.ml.feature.IDFModel
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

class SparkTagService implements TagService {

    SparkSession spark

    SparkTagService(SparkSession spark) {
        this.spark = spark
    }

    @Override
    void train(Map<String, List<String>> taggedTexts) {
        List<Row> data = Arrays.asList(
                RowFactory.create(0.0d, "Hi I heard about Spark"),
                RowFactory.create(0.0d, "I wish Java could use case classes"),
                RowFactory.create(1.0d, "Logistic regression models are neat")
        );
        StructType schema = new StructType([
            new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
            new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
        ].toArray(new StructField[0]) as StructField[]);
        Dataset<Row> sentenceData = spark.createDataFrame(data, schema);
        Tokenizer tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
        Dataset<Row> wordsData = tokenizer.transform(sentenceData);
        int numFeatures = 20;
        HashingTF hashingTF = new HashingTF()
                .setInputCol("words")
                .setOutputCol("rawFeatures")
                .setNumFeatures(numFeatures);
        Dataset<Row> featurizedData = hashingTF.transform(wordsData);
// alternatively, CountVectorizer can also be used to get term frequency vectors

        IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
        IDFModel idfModel = idf.fit(featurizedData);
        Dataset<Row> rescaledData = idfModel.transform(featurizedData);
        for (Row r : rescaledData.select("features", "label").takeAsList(3)) {
            def features = r.getAs(0);
            Double label = r.getDouble(1);
            System.out.println('features:' + features);
            System.out.println('label:' + label);
        }

        def xxx = new LogisticRegression().fit(rescaledData)

        data = Arrays.asList(
                RowFactory.create(100d, "Logistic regression models are bad"),
                RowFactory.create(100d, "Logistic regression models Logistic regression models"),
                RowFactory.create(101d, "a b c d e"),
                RowFactory.create(102d, "I wish Java foo bar models"),
                RowFactory.create(102d, "xxx Logistic yyy regression zzz models zzz are ooo neat xxx"),
                RowFactory.create(102d, "neat"),
                RowFactory.create(102d, "Java Spark"),
                RowFactory.create(102d, "Logistic regression foo bar baz")


        );
        schema = new StructType([
                new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
        ].toArray(new StructField[0]) as StructField[]);
        sentenceData = spark.createDataFrame(data, schema);
        tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
        wordsData = tokenizer.transform(sentenceData);
        numFeatures = 20;
        hashingTF = new HashingTF()
                .setInputCol("words")
                .setOutputCol("rawFeatures")
                .setNumFeatures(numFeatures);
        featurizedData = hashingTF.transform(wordsData);

        idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
        idfModel = idf.fit(featurizedData);
        rescaledData = idfModel.transform(featurizedData);
//        for (Row r : rescaledData.select("features", "label").takeAsList(3)) {
//            def features = r.getAs(0);
//            Double label = r.getDouble(1);
//            System.out.println('features:' + features);
//            System.out.println('label:' + label);
//        }

        def yyy = xxx.transform(rescaledData)
        yyy.show()

        Thread.sleep(30 * 1000)
    }

    @Override
    List<TagPredicition> predict(Map<String, String> texts) {
        return null
    }

    public static void main(String[] args) {
        new SparkTagService(SparkSession.builder()
                .master("local[*]")
                .getOrCreate()).train([:])
    }

    static class Textxxx {

        Textxxx(double category, String text) {
            this.category = category
            this.text = text
        }
        double category

        String text

    }

}