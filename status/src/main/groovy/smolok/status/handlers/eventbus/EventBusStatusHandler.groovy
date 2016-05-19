package smolok.status.handlers.eventbus

import org.springframework.beans.factory.annotation.Autowired
import smolok.lib.vertx.AmqpProbe
import smolok.status.StatusSubjectHandler
import smolok.status.SubjectStatus
import smolok.status.TcpEndpointStatusSubject

class EventBusStatusHandler implements StatusSubjectHandler {

    @Autowired
    private final AmqpProbe amqpProbe

    EventBusStatusHandler(AmqpProbe amqpProbe) {
        this.amqpProbe = amqpProbe
    }

    @Override
    boolean supports(Object statusSubject) {
        if(statusSubject instanceof TcpEndpointStatusSubject) {
            return statusSubject.name == 'eventbus'
        }
        false
    }

    @Override
    SubjectStatus statusOfSubject(Object subject) {
        def eventBusSubject = (TcpEndpointStatusSubject) subject
        amqpProbe.canSendMessageTo(eventBusSubject.host, eventBusSubject.port) ?
                new SubjectStatus('eventbus.canSend', "${true}", false) :
                new SubjectStatus('eventbus.canSend', "${false}", true)
    }

}
