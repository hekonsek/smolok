/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.smolok.adapter.rest.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableMap
import org.assertj.core.api.Assertions
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import smolok.bootstrap.Smolok
import smolok.encoding.spi.PayloadEncoding
import smolok.service.binding.ServiceBinding
import smolok.service.binding.ServiceEventProcessor

import static org.assertj.core.api.Assertions.assertThat
import static smolok.eventbus.client.Header.smolokHeaderKey
import static smolok.lib.common.Networks.findAvailableTcpPort

@RunWith(SpringRunner)
@SpringBootTest(classes = [Smolok, RestProtocolAdapterTest])
class RestProtocolAdapterTest {

    // Collaborators fixtures

    def json = new ObjectMapper()

    def rest = new RestTemplate()

    // Configuration fixtures

    static int restPort = findAvailableTcpPort()

    String baseURL = "http://localhost:${restPort}/test/"

    @BeforeClass
    static void beforeCloudPlatformStarted() {
        System.setProperty("rest.port", restPort + "");
    }

    @Autowired
    PayloadEncoding payloadEncoding

    // Tests

    @Test
    void shouldInvokeGetOperation() {
        Map response = json.readValue(new URL(baseURL + "count/1"), Map.class);
        assertThat(response.get("payload")).isEqualTo(1);
    }

    @Test
    void shouldInvokeGetOperationWithArgumentHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.put(smolokHeaderKey(0), ['1'])
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        def response = rest.exchange(baseURL + "count", HttpMethod.GET, entity, Map.class).body
        assertThat(response.get("payload")).isEqualTo(1);
    }

    @Test
    void shouldInvokePostOperation() {
        byte[] request = payloadEncoding.encode(ImmutableMap.of("foo", "bar"));
        Object payload = rest.postForObject(baseURL + "sizeOf", request, Map.class).get("payload");
        assertThat(payload).isEqualTo(1);
    }

    @Test
    public void shouldUseUriAndBody() {
        byte[] request = payloadEncoding.encode(ImmutableMap.of("foo", "bar"));
        Object payload = rest.postForObject(baseURL + "numberPlusSizeOf/1", request, Map.class).get("payload");
        assertThat(payload).isEqualTo(2);
    }

    @Test
    public void shouldHandleOptions() {
        Set<HttpMethod> options = rest.optionsForAllow(baseURL + "count/1");
        assertThat(options).isEmpty();
    }

    // Beans fixtures

    public interface TestService {

        int count(int number);

        int sizeOf(Map map);

        int numberPlusSizeOf(int number, Map map);

    }

    @Component("test")
    public static class TestInterfaceImpl implements TestService {

        @Override
        public int count(int number) {
            return number;
        }

        @Override
        public int sizeOf(Map map) {
            return map.size();
        }

        @Override
        public int numberPlusSizeOf(int number, Map map) {
            return number + map.size();
        }

    }

    @Bean
    ServiceBinding testServiceBinding(ServiceEventProcessor serviceEventProcessor) {
        new ServiceBinding(serviceEventProcessor, "test");
    }

}