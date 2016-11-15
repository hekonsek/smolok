package net.smolok.service.machinelearning.spark

import net.smolok.service.machinelearning.api.FeatureVector
import net.smolok.service.machinelearning.api.MachineLearningService
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.IDF
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

class SparkMachineLearningService implements MachineLearningService {

    private final SparkSession spark

    private final FeatureVectorStore featureVectorStore

    SparkMachineLearningService(SparkSession spark, FeatureVectorStore featureVectorStore) {
        this.spark = spark
        this.featureVectorStore = featureVectorStore
    }

    @Override
    void storeTrainingData(String collection, FeatureVector featureVector) {
        if(featureVectorStore instanceof ReadWriteFeatureVectorStore) {
            (featureVectorStore as ReadWriteFeatureVectorStore).write(collection, featureVector)
        }
    }

    @Override
    List<Double> predict(String collection, FeatureVector featureVector) {
        if(featureVectorStore instanceof ReadWriteFeatureVectorStore) {
            def data = (featureVectorStore as ReadWriteFeatureVectorStore).read(collection).collect {
                RowFactory.create(it.targetFeature, it.text)
            }
            if (data.isEmpty()) {
                throw new IllegalStateException()
            }

            def schema = new StructType([
                    new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                    new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
            ].toArray(new StructField[0]) as StructField[]);
            def featuresDataFrame = spark.createDataFrame(data, schema)
            def tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
            featuresDataFrame = tokenizer.transform(featuresDataFrame);
            int numFeatures = 20;
            HashingTF hashingTF = new HashingTF()
                    .setInputCol("words")
                    .setOutputCol("rawFeatures")
                    .setNumFeatures(numFeatures);
            def featurizedData = hashingTF.transform(featuresDataFrame)
            def idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
            def idfModel = idf.fit(featurizedData)
            def rescaledData = idfModel.transform(featurizedData);


            def xxx = new LogisticRegression().fit(rescaledData)

            data = Arrays.asList(
                    RowFactory.create(100d, featureVector.text)
            );
            schema = new StructType([
                    new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                    new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
            ].toArray(new StructField[0]) as StructField[]);
            featuresDataFrame = spark.createDataFrame(data, schema);
            tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
            featuresDataFrame = tokenizer.transform(featuresDataFrame);
//        numFeatures = 20;
            hashingTF = new HashingTF()
                    .setInputCol("words")
                    .setOutputCol("rawFeatures")
                    .setNumFeatures(numFeatures);
            featurizedData = hashingTF.transform(featuresDataFrame);

            idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
            idfModel = idf.fit(featurizedData);
            rescaledData = idfModel.transform(featurizedData);

            def yyy = xxx.transform(rescaledData)
            def prob = yyy.collectAsList().first().getAs(6)

            [(prob as DenseVector).values()[1]]
        } else {
            throw new IllegalStateException()
        }
    }

}