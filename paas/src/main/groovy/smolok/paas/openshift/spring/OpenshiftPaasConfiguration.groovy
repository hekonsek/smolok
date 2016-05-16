package smolok.paas.openshift.spring

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.process.DefaultProcessManager
import smolok.lib.process.ProcessManager
import smolok.paas.Paas
import smolok.paas.openshift.OpenshiftPaas

@Configuration
class OpenshiftPaasConfiguration {

    @Bean
    Paas paas(ProcessManager processManager) {
        new OpenshiftPaas(processManager)
    }

    @Bean(destroyMethod = 'close')
    @ConditionalOnMissingBean
    ProcessManager processManager() {
        new DefaultProcessManager()
    }

}