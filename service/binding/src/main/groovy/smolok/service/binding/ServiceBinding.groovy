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

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory
import smolok.service.binding.camel.CamelOperationBindingFactory

import static java.lang.String.format;

/**
 * Service binding is a general purpose backend service which can be used to bind communication coming through Event Bus
 * to the POJO service bean.
 */
class ServiceBinding extends RouteBuilder {

    // Static collaborators

    private static final Logger LOG = LoggerFactory.getLogger(ServiceBinding.class);

    // Constants

    private static final String TARGET_PROPERTY = "target";

    // Member collaborators

    protected final AuthenticationProvider authenticationProvider

    // Member configuration

    protected final String serviceChannel

    // Constructors

    ServiceBinding(AuthenticationProvider authenticationProvider, String serviceChannel) {
        this.authenticationProvider = authenticationProvider
        this.serviceChannel = serviceChannel
    }

    @Override
    public void configure() throws Exception {
        String fromChannel = format("amqp:%s.>?concurrentConsumers=20", serviceChannel);
        LOG.debug("Starting route consuming from channel: {}", fromChannel);

        from(fromChannel).process { exchange ->
            def credentials = authenticationProvider.authenticate(exchange)

            def message = exchange.getIn()
            def channel = message.getHeader('JMSDestination').toString()
            def operationBinding = new CamelOperationBindingFactory(context.registry).operationBinding(credentials, channel, message.body, message.getHeaders())
            exchange.setProperty(TARGET_PROPERTY, "bean:" + operationBinding.service() + "?method=" + operationBinding.operation() + "&multiParameterArray=true");
            exchange.setProperty("RETURN_TYPE", operationBinding.operationMethod().getReturnType());
            message.setBody(new Camels().convert(getContext(), operationBinding.arguments(), operationBinding.operationMethod().getParameterTypes()));
        }.toD(format('${property.%s}', TARGET_PROPERTY)).process { it ->
            Class returnType = it.getProperty("RETURN_TYPE", Class.class);
            if (Void.TYPE == returnType) {
                it.getIn().setBody(null);
            }
            it.getIn().setBody(it.getIn().getBody())
        }
    }

}
