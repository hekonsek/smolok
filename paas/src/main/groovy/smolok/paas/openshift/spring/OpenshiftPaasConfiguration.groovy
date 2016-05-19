package smolok.paas.openshift.spring

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.process.DefaultProcessManager
import smolok.lib.process.ProcessManager
import smolok.lib.vertx.AmqpProbe
import smolok.paas.Paas
import smolok.paas.openshift.OpenshiftPaas

/**
 * Spring configuration for OpenShift PaaS provider
 */
@Configuration
class OpenshiftPaasConfiguration {

    @Bean
    @ConditionalOnMissingBean
    Paas paas(ProcessManager processManager, AmqpProbe amqpProbe) {
        new OpenshiftPaas(processManager, amqpProbe)
    }

    @Bean(destroyMethod = 'close')
    @ConditionalOnMissingBean
    ProcessManager processManager() {
        new DefaultProcessManager()
    }

}