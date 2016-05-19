package smolok.status

import groovy.transform.Immutable

interface StatusSubjectHandler {

    boolean supports(Object statusSubject)

    SubjectStatus statusOfSubject(Object subject)

}