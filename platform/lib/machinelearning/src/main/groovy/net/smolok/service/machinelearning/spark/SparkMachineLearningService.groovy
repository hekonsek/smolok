package net.smolok.service.machinelearning.spark

import net.smolok.service.machinelearning.api.FeatureVector
import net.smolok.service.machinelearning.api.MachineLearningService
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.IDF
import org.apache.spark.ml.feature.IDFModel
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

class SparkMachineLearningService implements MachineLearningService {

    private final SparkSession spark

    SparkMachineLearningService(SparkSession spark) {
        this.spark = spark
    }

    private final Map<String, List<FeatureVector>> featureVectors = [:].withDefault{[]}

    @Override
    void storeTrainingData(String collection, FeatureVector featureVector) {
        featureVectors[collection] << featureVector
    }

    @Override
    List<Double> predict(String collection, FeatureVector featureVector) {
        def data = featureVectors[collection].collect{ RowFactory.create(it.targetFeature, it.text) }
        if(data.isEmpty()) {
            throw new IllegalStateException()
        }

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
        IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
        IDFModel idfModel = idf.fit(featurizedData);
        Dataset<Row> rescaledData = idfModel.transform(featurizedData);


        def xxx = new LogisticRegression().fit(rescaledData)

        data = Arrays.asList(
                RowFactory.create(100d, featureVector.text)
        );
        schema = new StructType([
                new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
        ].toArray(new StructField[0]) as StructField[]);
        sentenceData = spark.createDataFrame(data, schema);
        tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
        wordsData = tokenizer.transform(sentenceData);
//        numFeatures = 20;
        hashingTF = new HashingTF()
                .setInputCol("words")
                .setOutputCol("rawFeatures")
                .setNumFeatures(numFeatures);
        featurizedData = hashingTF.transform(wordsData);

        idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
        idfModel = idf.fit(featurizedData);
        rescaledData = idfModel.transform(featurizedData);
        for (Row r : rescaledData.select("features", "label").takeAsList(3)) {
            def features = r.getAs(0);
            Double label = r.getDouble(1);
            System.out.println('features:' + features);
            System.out.println('label:' + label);
        }

        def yyy = xxx.transform(rescaledData)
        yyy.show()
        def prob = yyy.collectAsList().first().getAs(6)
        def pred =  yyy.collectAsList().first().getAs(7)

        [(prob as DenseVector).values()[1]]
    }

    public static void main(String[] args) {
        def serv = new SparkMachineLearningService(SparkSession.builder()
                .master("local[*]")
                .getOrCreate())
        serv.storeTrainingData('col', new FeatureVector(text: 'Hi I heard about Spark', targetFeature: 0.0d))
        serv.storeTrainingData('col', new FeatureVector(text: 'I wish Java could use case classes', targetFeature: 0.0d))
        serv.storeTrainingData('col', new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1.0d))
//        serv.storeTrainingData('col', new FeatureVector(text: 'foo bar baz', targetFeature: 1.0d))

//        serv.storeTrainingData('col', new FeatureVector(text: 'no key words here', targetFeature: 0.0d))


//        def ooo = serv.predict('col', new FeatureVector(text: 'foo bar baz'))
        def nope = serv.predict('col', new FeatureVector(text: 'foo bar baz'))

        println nope
    }

}