package smolok.status

interface MetricSubjectHandler<T> {

    boolean supports(T metricSubject)

    Metric metric(T metricSubject)

}