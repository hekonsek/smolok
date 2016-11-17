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
package net.smolok.service.machinelearning.spark.spring

import net.smolok.service.binding.ServiceBinding
import net.smolok.service.binding.ServiceBindingFactory
import net.smolok.service.machinelearning.api.MachineLearningService
import net.smolok.service.machinelearning.spark.FeatureVectorStore
import net.smolok.service.machinelearning.spark.InMemoryFeatureVectorStore
import net.smolok.service.machinelearning.spark.SparkMachineLearningService
import org.apache.spark.sql.SparkSession
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SparkMachineLearningServiceConfiguration {

    @Bean(name = 'machinelearning')
    MachineLearningService machineLearningService(SparkSession spark, FeatureVectorStore featureVectorStore) {
        new SparkMachineLearningService(spark, featureVectorStore)
    }

    @Bean
    @ConditionalOnMissingBean
    FeatureVectorStore featureVectorStore() {
        new InMemoryFeatureVectorStore()
    }

    // Event bus wiring

    @Bean
    ServiceBinding machineLearningServiceBinding(ServiceBindingFactory serviceBindingFactory) {
        serviceBindingFactory.serviceBinding('machinelearning')
    }

}
