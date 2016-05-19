package smolok.status.kubernetes.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.process.ProcessManager
import smolok.status.MetricSubjectsResolver
import smolok.status.kubernetes.KubernetesMetricSubjectsResolver

@Configuration
class KubernetesStatusSubjectsResolverConfiguration {

    @Bean
    MetricSubjectsResolver statusSubjectsResolver(ProcessManager processManager) {
        new KubernetesMetricSubjectsResolver(processManager)
    }

}
