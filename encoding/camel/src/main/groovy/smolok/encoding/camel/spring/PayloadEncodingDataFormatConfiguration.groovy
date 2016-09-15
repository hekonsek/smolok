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
package smolok.encoding.camel.spring;

import smolok.encoding.camel.PayloadEncodingDataFormat
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.encoding.spi.PayloadEncoding

@Configuration
class PayloadEncodingDataFormatConfiguration {

    @Bean
    PayloadEncodingDataFormat payloadEncodingDataFormat(PayloadEncoding payloadEncoding) {
        new PayloadEncodingDataFormat(payloadEncoding)
    }

}