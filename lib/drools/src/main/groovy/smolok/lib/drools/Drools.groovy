package smolok.lib.drools

import org.kie.api.KieServices
import org.kie.api.command.Command
import org.kie.server.api.model.KieContainerResource
import org.kie.server.api.model.ReleaseId
import org.kie.server.api.model.ServiceResponse
import org.kie.server.client.KieServicesClient
import org.kie.server.client.RuleServicesClient
import smolok.lib.drools.spring.DroolsConfiguration

import static org.kie.server.api.model.ServiceResponse.ResponseType.FAILURE

class Drools {

    private final KieServicesClient kieServicesClient

    Drools(KieServicesClient kieServicesClient) {
        this.kieServicesClient = kieServicesClient
    }

    void createContainer(String id, String groupId, String artifactId, String version) {
        def container = new KieContainerResource()
        container.releaseId = new ReleaseId(groupId, artifactId, version)
        def createResponse = kieServicesClient.createContainer(id, container)
        if(createResponse.getType() == FAILURE) {
            throw new RuntimeException(createResponse.msg)
        }
    }

    void insert(String container, Object fact) {
        def rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class)
        def commandsFactory = KieServices.Factory.get().getCommands()
        Command<?> insert = commandsFactory.newInsert(fact)
        ServiceResponse<String> executeResponse = rulesClient.executeCommands(container, insert);
        if(executeResponse.getType() == FAILURE) {
            throw new RuntimeException(executeResponse.getMsg())
        }
    }

}
