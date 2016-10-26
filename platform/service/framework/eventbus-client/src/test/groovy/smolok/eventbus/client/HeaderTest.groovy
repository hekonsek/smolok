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
package smolok.eventbus.client

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static smolok.eventbus.client.Header.arguments
import static smolok.eventbus.client.Header.header

class HeaderTest {

    @Test
    void shouldGenerateHeaderNameForArgument() {
        def argument = arguments('foo').first()
        assertThat(argument.key()).isEqualTo('SMOLOK_ARG0')
    }

    @Test
    void shouldConvertHeaderIntoMapEntry() {
        def argument = arguments(header('foo', 'bar'))
        assertThat(argument.foo).isEqualTo('bar')
    }

}
