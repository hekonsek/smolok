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
package net.smolok.service.binding

import com.fasterxml.jackson.databind.ObjectMapper
import net.smolok.service.binding.camel.Camels;
import org.apache.camel.builder.RouteBuilder
import org.slf4j.Logger;
import org.slf4j.LoggerFactory

import static java.lang.String.format
import static smolok.lib.common.Reflections.isContainer
import static smolok.lib.common.Reflections.isPojo;

/**
 * Service binding is a general purpose backend service which can be used to bind communication coming through Event Bus
 * to the POJO service bean.
 */
class ServiceBinding extends RouteBuilder {

    // Static collaborators

    private static final Logger LOG = LoggerFactory.getLogger(ServiceBinding.class);

    // Member collaborators

    protected final ServiceEventProcessor serviceEventProcessor

    // Member configuration

    protected final String serviceChannel

    // Constructors

    ServiceBinding(ServiceEventProcessor serviceEventProcessor, String serviceChannel) {
        this.serviceEventProcessor = serviceEventProcessor
        this.serviceChannel = serviceChannel
    }

    void start() {

    }

    protected Object onEvent(ServiceEvent serviceEvent) {
        // Generic
        def operationBinding = serviceEventProcessor.onEvent(serviceEvent)

        // Camel specific
        def body = new Camels().convert(getContext(), operationBinding.arguments(), operationBinding.operationMethod().getParameterTypes())
        def response = getContext().createProducerTemplate().requestBody("bean:" + operationBinding.service() + "?method=" + operationBinding.operation() + "&multiParameterArray=true", body)

        // Generic
        def returnType = operationBinding.operationMethod().returnType
        if (Void.TYPE == returnType) {
            null
            // This is workaround needed due to the fact that Qpid JMS client doesn't support nested maps.
        } else if(isPojo(returnType) || isContainer(returnType)) {
            new ObjectMapper().writeValueAsBytes(response)
        } else {
            response
        }
    }

    @Override
    void configure() {
        String fromChannel = format("amqp:%s.>?concurrentConsumers=20", serviceChannel);
        LOG.debug("Starting route consuming from channel: {}", fromChannel);

        from(fromChannel).process { exchange ->
            def message = exchange.in
            def channel = message.getHeader('JMSDestination', String.class)
            exchange.in.body = onEvent(new ServiceEvent(channel, message.body, message.headers))
        }
    }

}
