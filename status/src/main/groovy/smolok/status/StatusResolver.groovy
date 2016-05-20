package smolok.status

import groovy.transform.CompileStatic

/**
 * Central access point for resolving status metrics.
 */
@CompileStatic
class StatusResolver {

    // Collaborators

    private final MetricSubjectsResolver subjectsResolver

    private final List<MetricSubjectHandler> subjectHandlers

    // Constructors

    StatusResolver(MetricSubjectsResolver subjectsResolver, List<MetricSubjectHandler> subjectHandlers) {
        this.subjectsResolver = subjectsResolver
        this.subjectHandlers = subjectHandlers
    }

    // Operations

    List<Metric> status() {
        List<Metric> status = []
        subjectsResolver.metricSubjects().each { subject ->
            subjectHandlers.each {
                if(it.supports(subject)) {
                    status += it.metric(subject)
                }
            }
        }
        status
    }

}
