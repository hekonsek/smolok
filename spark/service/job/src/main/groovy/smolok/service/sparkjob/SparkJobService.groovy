package smolok.service.sparkjob

import smolok.lib.spark.SparkSubmitResult

interface SparkJobService {

    void createJob(String jobUri)

    String jobUri(String jobId)

    SparkSubmitResult executeJob(String jobId)

}