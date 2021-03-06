package net.smolok.service.binding.camel

import com.fasterxml.jackson.databind.ObjectMapper
import net.smolok.service.binding.ServiceEvent
import org.apache.camel.spi.Registry
import org.apache.commons.lang3.Validate
import net.smolok.service.binding.security.Credentials
import net.smolok.service.binding.OperationBinding
import net.smolok.service.binding.OperationBindingFactory
import net.smolok.service.binding.Tenant

import java.lang.reflect.Method

import static java.util.Arrays.asList
import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.Reflections.isContainer
import static smolok.lib.common.Reflections.isPojo

class CamelOperationBindingFactory implements OperationBindingFactory {

    // Logger

    private static final LOG = getLogger(CamelOperationBindingFactory.class)

    private final Registry registry

    CamelOperationBindingFactory(Registry registry) {
        this.registry = registry
    }

    @Override
    OperationBinding operationBinding(Credentials credentials, ServiceEvent serviceEvent) {
        LOG.debug('Parsing operation binding for channel: {}', serviceEvent.channel())
        def normalizedChannel = serviceEvent.channel().substring(serviceEvent.channel().lastIndexOf('/') + 1)
        LOG.debug('Channel {} after normalization: {}', serviceEvent.channel(), normalizedChannel)

        String[] channelParts = normalizedChannel.split("\\.");
        String service = channelParts[0];
        String operation = channelParts[1];

        def bean = registry.lookupByName(service)
        Validate.notNull(bean, "Cannot find service with name '%s'.", service);
        Class beanType = bean.getClass();

        LOG.debug("Detected service bean type {} for operation: {}", beanType, operation);
        List<Method> beanMethods = new ArrayList<>(asList(beanType.getDeclaredMethods()));
        beanMethods.addAll(asList(beanType.getMethods()));
        Method operationMethod = beanMethods.find{method -> method.getName().equals(operation)}

        List<Object> arguments = new LinkedList<>(asList(channelParts).subList(2, channelParts.length))

        for(Map.Entry<String, Object> header : serviceEvent.headers().entrySet()) {
            if(header.getKey().startsWith("SMOLOK_ARG")) {
                arguments.add(header.getValue());
            }
        }

        def incomingPayload = serviceEvent.body()
        if (incomingPayload != null) {
            def expectedPayloadType = operationMethod.parameterTypes.last()
            if(incomingPayload.class == byte[].class && (isContainer(expectedPayloadType) || isPojo(expectedPayloadType))) {
                incomingPayload = new ObjectMapper().readValue(incomingPayload, expectedPayloadType)
            }
            arguments.add(incomingPayload)
        }

        def tenantPosition = operationMethod.parameterAnnotations.findIndexOf {
            it.find{ it.annotationType() == Tenant.class }
        }
        if(tenantPosition >= 0) {
            arguments.addAll(tenantPosition, credentials.tenant())
        }

        new OperationBinding(service, operationMethod, arguments)
    }

}
