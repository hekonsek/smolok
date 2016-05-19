package smolok.status.kubernetes

import smolok.lib.process.ProcessManager
import smolok.status.StatusSubjectsResolver
import smolok.status.TcpEndpointStatusSubject

import static smolok.lib.process.ExecutorBasedProcessManager.command

class KubernetesStatusSubjectsResolver implements StatusSubjectsResolver {

    private final ProcessManager processManager

    KubernetesStatusSubjectsResolver(ProcessManager processManager) {
        this.processManager = processManager
    }

    @Override
    List<Object> statusSubjects() {
        def output = processManager.execute(command('docker exec openshift-server oc get service'))
        def servicesOutput = output.subList(1, output.size())
        servicesOutput.collect{ it.split(/\s+/) }.collect {
            new TcpEndpointStatusSubject(it[0], it[1], it[3].replaceFirst('/.+', '').toInteger())
        }
    }

}