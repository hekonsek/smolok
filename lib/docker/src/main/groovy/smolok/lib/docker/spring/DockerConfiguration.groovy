package smolok.lib.docker.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.docker.Docker
import smolok.lib.process.ProcessManager

@Configuration
class DockerConfiguration {

    @Bean
    Docker docker(ProcessManager processManager) {
        new Docker(processManager)
    }

}
