package smolok.status.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.status.StatusResolver
import smolok.status.MetricSubjectHandler
import smolok.status.MetricSubjectsResolver

@Configuration
class StatusResolverConfiguration {

    @Bean
    StatusResolver statusResolver(MetricSubjectsResolver subjectsResolver, List<MetricSubjectHandler> subjectHandlers) {
        new StatusResolver(subjectsResolver, subjectHandlers)
    }

}
