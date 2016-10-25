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

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import smolok.bootstrap.Smolok
import net.smolok.paas.openshift.OpenShiftPaas

import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.MINUTES
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Awaitilities.condition

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Smolok.class)
class OpenShiftPaasConfigurationTest {

    // Test subject fixtures

    @Autowired
    OpenShiftPaas paas

    @Before
    void before() {
        paas.reset()
        paas.start()
    }

    // Tests

    @Test
    void shouldStart() {
        // Then
        assertThat(paas.started).isTrue()
    }

    @Test
    void shouldProvisionAfterStart() {
        // Then
        assertThat(paas.provisioned).isTrue()
    }

    @Test
    void shouldStop() {
        // When
        paas.stop()

        // Then
        assertThat(paas.started).isFalse()
    }

    @Test
    void shouldStopAfterReset() {
        // When
        paas.reset()

        // Then
        assertThat(paas.started).isFalse()
    }

    @Test
    void shouldStartEventBus() {
        // Then
        def eventBusService = paas.services().find { it.name == 'eventbus' }
        assertThat(eventBusService).isNotNull()
    }

    @Test
    void shouldStartConfigurationService() {
        // Then
        await().atMost(1, MINUTES).until condition {
            paas.oc('get pod').find{ it.contains('service-configuration') && it.contains('Running') } != null
        }
    }

    @Test
    void shouldNotStartEventBusTwice() {
        // When
        paas.start()

        // Then
        def eventBusService = paas.services().find { it.name == 'eventbus' }
        assertThat(eventBusService).isNotNull()
    }

    @Test
    void shouldStartMongoService() {
        // When
        paas.startService('mongo')

        // Then
        await().atMost(1, MINUTES).until(condition {paas.services().find { it.name == 'mongo' } != null})
        def mongoService = paas.services().find { it.name == 'mongo' }
        assertThat(mongoService).isNotNull()
    }

}
