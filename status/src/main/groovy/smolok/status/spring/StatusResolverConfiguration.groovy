package smolok.status.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.status.StatusResolver
import smolok.status.StatusSubjectHandler
import smolok.status.StatusSubjectsResolver

@Configuration
class StatusResolverConfiguration {

    @Bean
    StatusResolver statusResolver(StatusSubjectsResolver subjectsResolver, List<StatusSubjectHandler> subjectHandlers) {
        new StatusResolver(subjectsResolver, subjectHandlers)
    }

}
