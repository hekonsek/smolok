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
package net.smolok.service.configuration.filesystem.spring

import net.smolok.service.configuration.api.ConfigurationService
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static java.io.File.createTempFile
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Properties.setSystemStringProperty
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringRunner)
@SpringBootTest(classes = KapuaApplication)
class FileSystemConfigurationServiceTest {

    def key = uuid()

    def value = uuid()

    @BeforeClass
    static void beforeClass() {
        setSystemStringProperty('configuration.file', createTempFile('smolok', 'test').absolutePath)
    }

    // Tests subject

    @Autowired
    ConfigurationService configurationServiceClient

    // Tests

    @Test
    void shouldReadStoredProperty() {
        // Given
        configurationServiceClient.put(key, value)

        // When
        def property = configurationServiceClient.get(key)

        // Then
        assertThat(property).isEqualTo(value)
    }

    @Test
    void shouldReadNullProperty() {
        // When
        def property = configurationServiceClient.get(key)

        // Then
        assertThat(property).isNull()
    }

    @Test
    void shouldOverrideProperty() {
        // Given
        configurationServiceClient.put(key, value)
        configurationServiceClient.put(key, 'newValue')

        // When
        def property = configurationServiceClient.get(key)

        // Then
        assertThat(property).isEqualTo('newValue')
    }

}