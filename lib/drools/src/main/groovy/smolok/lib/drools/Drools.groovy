package smolok.lib.drools

import org.kie.api.KieServices
import org.kie.api.command.Command
import org.kie.api.command.KieCommands
import org.kie.server.api.model.KieContainerResource
import org.kie.server.api.model.ReleaseId
import org.kie.server.api.model.ServiceResponse
import org.kie.server.client.KieServicesClient
import org.kie.server.client.RuleServicesClient

class Drools {

    private final KieServicesClient kieServicesClient

    Drools(KieServicesClient kieServicesClient) {
        this.kieServicesClient = kieServicesClient
    }

    Drools() {
        this(DroolsConfigurer.newServicesClient())
    }

    void createContainer(String id) {
        def container = new KieContainerResource()
        container.releaseId = new ReleaseId('gr', 'art', '1.0')
        def createResponse = kieServicesClient.createContainer(id, container)
        if(createResponse.getType() == ServiceResponse.ResponseType.FAILURE) {
            throw new RuntimeException(createResponse.msg)
        }
    }

    void insert(String container, Object fact) {
        RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
        KieCommands commandsFactory = KieServices.Factory.get().getCommands();
        Command<?> insert = commandsFactory.newInsert(fact);
        ServiceResponse<String> executeResponse = rulesClient.executeCommands(container, insert);
        if(executeResponse.getType() == ServiceResponse.ResponseType.SUCCESS) {
            System.out.println("Commands executed with success! Response: ");
            System.out.println(executeResponse.getResult());
        }
        else {
            throw new RuntimeException(executeResponse.getMsg())
        }
    }

    public static void main(String[] args) {
        new Drools().createContainer('hello')
        new Drools().insert('hello', "foo")
    }

}
