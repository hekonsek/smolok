package smolok.status

import net.smolok.paas.Paas

class PaasMetricSubjectsResolver implements MetricSubjectsResolver {

    private final Paas paas

    PaasMetricSubjectsResolver(Paas paas) {
        this.paas = paas
    }

    @Override
    List<Object> metricSubjects() {
        paas.services()
    }

}