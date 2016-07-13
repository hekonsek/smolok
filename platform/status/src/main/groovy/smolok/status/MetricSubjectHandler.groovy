package smolok.status

import groovy.transform.CompileStatic

@CompileStatic
interface MetricSubjectHandler<T> {

    boolean supports(T metricSubject)

    List<Metric> metric(T metricSubject)

}