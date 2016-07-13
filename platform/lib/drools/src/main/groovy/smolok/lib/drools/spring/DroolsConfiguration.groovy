package smolok.lib.drools.spring

import org.kie.server.api.marshalling.MarshallingFormat
import org.kie.server.client.KieServicesClient
import org.kie.server.client.KieServicesConfiguration
import org.kie.server.client.KieServicesFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.drools.Drools

@Configuration
class DroolsConfiguration {

    private static final String URL = "http://localhost:8080/kie-server-6.4.0.Final-ee7/services/rest/server";
    private static final String USER = "kieserver";
    private static final String PASSWORD = "kieserver";

    private static final MarshallingFormat FORMAT = MarshallingFormat.JSON

    @Bean
    Drools drools(KieServicesClient kieServicesClient) {
        new Drools(kieServicesClient)
    }

    @Bean
    KieServicesClient kieServicesClient() {
        KieServicesConfiguration conf;
        conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
        conf.setMarshallingFormat(FORMAT);
        KieServicesFactory.newKieServicesClient(conf);
    }

}
