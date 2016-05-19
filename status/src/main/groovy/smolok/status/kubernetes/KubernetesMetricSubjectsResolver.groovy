package smolok.status.kubernetes

import smolok.lib.process.ProcessManager
import smolok.status.MetricSubjectsResolver
import smolok.status.TcpEndpointStatusSubject

import static smolok.lib.process.ExecutorBasedProcessManager.command

class KubernetesMetricSubjectsResolver implements MetricSubjectsResolver {

    private final ProcessManager processManager

    KubernetesMetricSubjectsResolver(ProcessManager processManager) {
        this.processManager = processManager
    }

    @Override
    List<Object> metricSubjects() {
        def output = processManager.execute(command('docker exec openshift-server oc get service'))
        def servicesOutput = output.subList(1, output.size())
        servicesOutput.collect{ it.split(/\s+/) }.collect {
            new TcpEndpointStatusSubject(it[0], it[1], it[3].replaceFirst('/.+', '').toInteger())
        }
    }

}