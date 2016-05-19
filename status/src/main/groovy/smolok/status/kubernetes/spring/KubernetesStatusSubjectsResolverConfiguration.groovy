package smolok.status.kubernetes.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.process.ProcessManager
import smolok.status.StatusSubjectsResolver
import smolok.status.kubernetes.KubernetesStatusSubjectsResolver

@Configuration
class KubernetesStatusSubjectsResolverConfiguration {

    @Bean
    StatusSubjectsResolver statusSubjectsResolver(ProcessManager processManager) {
        new KubernetesStatusSubjectsResolver(processManager)
    }

}
