package smolok.status.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.paas.Paas
import smolok.status.StatusResolver
import smolok.status.MetricSubjectHandler
import smolok.status.MetricSubjectsResolver
import smolok.status.PaasMetricSubjectsResolver

@Configuration
class StatusResolverConfiguration {

    @Bean
    StatusResolver statusResolver(MetricSubjectsResolver subjectsResolver, List<MetricSubjectHandler> subjectHandlers) {
        new StatusResolver(subjectsResolver, subjectHandlers)
    }

    @Bean
    MetricSubjectsResolver statusSubjectsResolver(Paas paas) {
        new PaasMetricSubjectsResolver(paas)
    }

}
