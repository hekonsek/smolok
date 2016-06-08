/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package smolok.service.binding;

import org.apache.camel.spi.Registry;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.lang.reflect.Method

import static java.util.Arrays.asList
import static org.slf4j.LoggerFactory.getLogger;

class OperationBinding {

    // Logger

    private static final LOG = getLogger(OperationBinding.class)

    private final String service

    private final Method operationMethod

    private final List<?> arguments

    OperationBinding(String service, Method operationMethod, List<?> arguments) {
        this.service = service
        this.operationMethod = operationMethod
        this.arguments = arguments
    }

    static OperationBinding operationBinding(Credentials credentials, String channel, Object incomingPayload, Map<String, Object> headers, Registry registry) {
        String rawChannel = channel.substring(channel.lastIndexOf('/') + 1);
        String[] channelParts = rawChannel.split("\\.");
        String service = channelParts[0];
        String operation = channelParts[1];

        Object bean = registry.lookupByName(service);
        Validate.notNull(bean, "Cannot find service with name '%s'.", service);
        Class beanType = bean.getClass();

        LOG.debug("Detected service bean type {} for operation: {}", beanType, operation);
        List<Method> beanMethods = new ArrayList<>(asList(beanType.getDeclaredMethods()));
        beanMethods.addAll(asList(beanType.getMethods()));
        Method operationMethod = beanMethods.find{method -> method.getName().equals(operation)}

        List<Object> arguments = new LinkedList<>(asList(channelParts).subList(2, channelParts.length))

        for(Map.Entry<String, Object> header : headers.entrySet()) {
            if(header.getKey().startsWith("SMOLOK_ARG")) {
                arguments.add(header.getValue());
            }
        }

        if (incomingPayload != null) {
            Object payload = incomingPayload;
            arguments.add(payload);
        }

        def tenantPosition = operationMethod.parameterAnnotations.findIndexOf {
            it.find{ it.annotationType() == Tenant.class }
        }
        if(tenantPosition >= 0) {
            arguments.addAll(tenantPosition, credentials.tenant())
        }

        new OperationBinding(service, operationMethod, arguments)
    }

    String service() {
        service;
    }

    String operation() {
        operationMethod.name
    }

    public List<?> arguments() {
        return arguments;
    }

    public Method operationMethod() {
        return operationMethod;
    }

    @Override
    String toString() {
        ReflectionToStringBuilder.toString(this)
    }

}