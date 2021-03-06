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
package net.smolok.paas.openshift.spring

import net.smolok.lib.download.DownloadManager
import net.smolok.paas.MachineLearningServiceImageLocatorResolver
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.process.ProcessManager
import smolok.lib.vertx.AmqpProbe
import net.smolok.paas.DeviceServiceImageLocatorResolver
import net.smolok.paas.ImageLocatorResolver
import net.smolok.paas.Paas
import net.smolok.paas.RestAdapterImageLocatorResolver
import net.smolok.paas.openshift.OpenShiftPaas

/**
 * Spring configuration for OpenShift PaaS provider
 */
@Configuration
class OpenshiftPaasConfiguration {

    /**
     * OpenShift PaaS instance. Can be overridden.
     */
    @Bean(initMethod = 'init')
    @ConditionalOnMissingBean
    Paas paas(DownloadManager downloadManager, ProcessManager processManager, AmqpProbe amqpProbe, List<ImageLocatorResolver> imageLocatorResolvers) {
        new OpenShiftPaas(downloadManager, processManager, amqpProbe, imageLocatorResolvers)
    }

    @Bean
    DeviceServiceImageLocatorResolver deviceServiceImageLocatorResolver() {
        new DeviceServiceImageLocatorResolver()
    }

    @Bean
    RestAdapterImageLocatorResolver restAdapterImageLocatorResolver() {
        new RestAdapterImageLocatorResolver()
    }

    @Bean
    MachineLearningServiceImageLocatorResolver machineLearningServiceImageLocatorResolver() {
        new MachineLearningServiceImageLocatorResolver()
    }

}