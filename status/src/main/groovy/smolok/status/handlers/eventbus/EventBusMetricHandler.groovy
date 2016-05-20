package smolok.status.handlers.eventbus

import smolok.lib.vertx.AmqpProbe
import smolok.paas.ServiceEndpoint
import smolok.status.MetricSubjectHandler
import smolok.status.Metric

import static org.slf4j.LoggerFactory.getLogger
import static smolok.status.Metric.metric

class EventBusMetricHandler implements MetricSubjectHandler<ServiceEndpoint> {

    private final static LOG = getLogger(EventBusMetricHandler.class)

    // Constants

    public static final String METRIC_KEY = 'eventbus.canSend'

    // Collaborators

    private final AmqpProbe amqpProbe

    // Constructors

    EventBusMetricHandler(AmqpProbe amqpProbe) {
        this.amqpProbe = amqpProbe
    }

    // Handler operations

    @Override
    boolean supports(ServiceEndpoint metricSubject) {
        LOG.debug('Checking if {} is supported by this handler.', metricSubject)
        metricSubject instanceof ServiceEndpoint &&
                metricSubject.name == 'eventbus'
    }

    @Override
    List<Metric> metric(ServiceEndpoint subject) {
        LOG.debug('Generating metric for subject: {}', subject)
        [amqpProbe.canSendMessageTo(subject.host, subject.port) ?
                metric(METRIC_KEY, true) :
                metric(METRIC_KEY, false, true)]
    }

}
