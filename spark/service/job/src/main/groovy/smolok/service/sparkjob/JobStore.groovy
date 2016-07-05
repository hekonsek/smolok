package smolok.service.sparkjob

interface JobStore {

   void save(String jobId, String jobUri)

    String jobUri(String jobId)

}