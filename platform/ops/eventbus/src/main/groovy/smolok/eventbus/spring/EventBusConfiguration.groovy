/**
 * Licensed to the Smolok under one or more
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
package smolok.eventbus.spring

import org.apache.activemq.broker.BrokerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for EventBus service. Current implementation is based on ActiveMQ.
 */
@Configuration
class EventBusConfiguration {

    @Bean(initMethod = 'start', destroyMethod = 'stop')
    BrokerService broker(
            @Value('${EVENTBUS_SERVICE_HOST:0.0.0.0}') String host,
            @Value('${EVENTBUS_SERVICE_PORT:5672}') int port,
            @Value('${EVENTBUS_PERSISTENCE:false}') boolean persistence) {
        def broker = new BrokerService()
        broker.setPersistent(persistence)
        broker.addConnector("amqp://${host}:${port}")
        broker
    }

}