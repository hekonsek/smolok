package smolok.service.sparkjob

class InMemoryJobStore implements JobStore {

    private final Map<String, String> jobs = [:]

    @Override
    void save(String jobId, String jobUri) {
        jobs[jobId] = jobUri
    }

    @Override
    String jobUri(String jobId) {
        jobs[jobId]
    }

}
