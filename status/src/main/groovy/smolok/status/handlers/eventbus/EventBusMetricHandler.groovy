package smolok.status.handlers.eventbus

import smolok.lib.vertx.AmqpProbe
import smolok.status.MetricSubjectHandler
import smolok.status.Metric
import smolok.status.TcpEndpointStatusSubject

import static org.slf4j.LoggerFactory.getLogger
import static smolok.status.Metric.metric

class EventBusMetricHandler implements MetricSubjectHandler<TcpEndpointStatusSubject> {

    private final static LOG = getLogger(EventBusMetricHandler.class)

    public static final String METRIC_KEY = 'eventbus.canSend'

    private final AmqpProbe amqpProbe

    // Constructors

    EventBusMetricHandler(AmqpProbe amqpProbe) {
        this.amqpProbe = amqpProbe
    }

    // Handler operations

    @Override
    boolean supports(TcpEndpointStatusSubject metricSubject) {
        LOG.debug('Checking if {} is supported by this handler.', metricSubject)
        metricSubject instanceof TcpEndpointStatusSubject &&
                metricSubject.name == 'eventbus'
    }

    @Override
    Metric metric(TcpEndpointStatusSubject subject) {
        amqpProbe.canSendMessageTo(subject.host, subject.port) ?
                metric(METRIC_KEY, true) :
                new Metric(METRIC_KEY, false, true)
    }

}
