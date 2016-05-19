package smolok.status

class StatusResolver {

    private final MetricSubjectsResolver subjectsResolver

    private final List<MetricSubjectHandler> subjectHandlers

    StatusResolver(MetricSubjectsResolver subjectsResolver, List<MetricSubjectHandler> subjectHandlers) {
        this.subjectsResolver = subjectsResolver
        this.subjectHandlers = subjectHandlers
    }

    List<Metric> status() {
        def status = []
        subjectsResolver.metricSubjects().each { subject ->
            subjectHandlers.each {
                if(it.supports(subject)) {
                    status << it.metric(subject)
                }
            }
        }
        status
    }

}
