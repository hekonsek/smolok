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
package net.smolok.cmd.core

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class BaseCommandTest {

    def command = new TestCommand()

    // Tests

    @Test
    void shouldSupportCommand() {
        def supports = command.supports('this', 'is', 'my', 'command')
        assertThat(supports).isTrue()
    }

    @Test
    void shouldSupportCommandWithArgument() {
        def supports = command.supports('this', 'is', 'my', 'command', 'argument')
        assertThat(supports).isTrue()
    }

    @Test
    void shouldNotSupportPartialCommand() {
        def supports = command.supports('this', 'is', 'my')
        assertThat(supports).isFalse()
    }

    @Test
    void shouldParseOption() {
        def fooValue = command.option(['--foo=bar'] as String[], 'foo')
        assertThat(fooValue).isPresent().contains('bar')
    }

    @Test
    void shouldProvideDefaultValueForOption() {
        def fooValue = command.option([''] as String[], 'foo', 'bar')
        assertThat(fooValue).isEqualTo('bar')
    }

    @Test
    void shouldRequestHelp() {
        def helpRequested = command.helpRequested('this', 'is', 'my', 'command', '--help')
        assertThat(helpRequested).isTrue()
    }

}