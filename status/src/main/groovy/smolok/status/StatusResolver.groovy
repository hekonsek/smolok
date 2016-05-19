package smolok.status

class StatusResolver {

    private final StatusSubjectsResolver subjectsResolver

    private final List<StatusSubjectHandler> subjectHandlers

    StatusResolver(StatusSubjectsResolver subjectsResolver, List<StatusSubjectHandler> subjectHandlers) {
        this.subjectsResolver = subjectsResolver
        this.subjectHandlers = subjectHandlers
    }

    List<SubjectStatus> status() {
        def status = []
        subjectsResolver.statusSubjects().each { subject ->
            subjectHandlers.each {
                if(it.supports(subject)) {
                    status << it.statusOfSubject(subject)
                }
            }
        }
        status
    }

}
