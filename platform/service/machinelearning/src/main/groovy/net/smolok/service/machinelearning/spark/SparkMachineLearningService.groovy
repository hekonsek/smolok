package net.smolok.service.machinelearning.spark

import net.smolok.service.machinelearning.api.FeatureVector
import net.smolok.service.machinelearning.api.MachineLearningService
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.classification.LogisticRegressionModel
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

    private final Map<String, LogisticRegressionModel> models = [:]

    SparkMachineLearningService(SparkSession spark, FeatureVectorStore featureVectorStore) {
        this.spark = spark
        this.featureVectorStore = featureVectorStore
    }

    @Override
    void storeTrainingData(String collection, FeatureVector... featureVectors) {
        if(featureVectorStore instanceof ReadWriteFeatureVectorStore) {
            featureVectors.each {
                (featureVectorStore as ReadWriteFeatureVectorStore).write(collection, it)
            }
            def ungroupedData = (featureVectorStore as ReadWriteFeatureVectorStore).read(collection)
            def labels = ungroupedData.collect{ it.targetLabel }.unique()
            labels.each { label ->
                def data = ungroupedData.findAll { it.targetLabel == label }.collect {
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

                if(label == null) {
                    label = 'default'
                }
                models[label] = new LogisticRegression().fit(rescaledData)
            }
        }
    }

    @Override
    Map<String, Double> predict(String collection, FeatureVector featureVector) {
        def labelConfidence = [:]
        if(featureVectorStore instanceof ReadWriteFeatureVectorStore) {
            def ungroupedData = (featureVectorStore as ReadWriteFeatureVectorStore).read(collection)
            def labels = ungroupedData.collect{ it.targetLabel }.unique()
            labels.each { label ->
                if(label == null) {
                    label = 'default'
                }
                def regressionModel = models[label]

                def data = [RowFactory.create(100d, featureVector.text)]
                def schema = new StructType([
                        new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                        new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
                ].toArray(new StructField[0]) as StructField[]);
                def featuresDataFrame = spark.createDataFrame(data, schema);
                def tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
                featuresDataFrame = tokenizer.transform(featuresDataFrame);
                def hashingTF = new HashingTF()
                        .setInputCol("words")
                        .setOutputCol("rawFeatures")
                        .setNumFeatures(20)
                def featurizedData = hashingTF.transform(featuresDataFrame);

                def idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
                def idfModel = idf.fit(featurizedData);
                def rescaledData = idfModel.transform(featurizedData);

                def predictions = regressionModel.transform(rescaledData)
                def prob = predictions.collectAsList().first().getAs(6)

                labelConfidence[label] = (prob as DenseVector).values()[1]
            }

            labelConfidence
        } else {
            throw new IllegalStateException()
        }
    }

}