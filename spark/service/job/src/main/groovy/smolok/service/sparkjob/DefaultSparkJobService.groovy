package smolok.service.sparkjob

import smolok.lib.spark.SparkSubmit
import smolok.lib.spark.SparkSubmitCommand
import smolok.lib.spark.SparkSubmitResult

class DefaultSparkJobService implements SparkJobService {

    private final JobStore jobStore

    private final SparkSubmit sparkSubmit

    DefaultSparkJobService(JobStore jobStore, SparkSubmit sparkSubmit) {
        this.jobStore = jobStore
        this.sparkSubmit = sparkSubmit
    }

    @Override
    void createJob(String jobUri) {
        def jobId = jobUri.substring(0, jobUri.indexOf(':'))
        jobStore.save(jobId, jobUri)
    }

    @Override
    String jobUri(String jobId) {
        jobStore.jobUri(jobId)
    }

    @Override
    SparkSubmitResult executeJob(String jobId) {
        def jobUri = jobStore.jobUri(jobId)
        if(jobUri.matches(/.+?:file:.*/)) {
            def path = jobUri.replaceFirst(/.+?:file:/, '')
            return sparkSubmit.submit(new SparkSubmitCommand(path))
        }
    }

}
