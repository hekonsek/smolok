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
package net.smolok.cmd.commands

import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import smolok.lib.docker.Container
import smolok.lib.docker.Docker

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.BDDMockito.given
import static smolok.lib.process.ExecutorBasedProcessManager.command

class SparkSubmitCommandTest {

    def docker = Mockito.mock(Docker.class)

    def containerCaptor = ArgumentCaptor.forClass(Container.class)

    @Test
    void shouldPassArguments() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.arguments()).isEqualTo(['--master=spark://localhost:7077', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldSkipOptionsAndFindJobArtifact() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit --master=foo artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.arguments()).isEqualTo(['--master=foo', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldCleanUpContainerByDefault() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit --master=foo artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.cleanUp()).isEqualTo(true)
        assertThat(container.arguments()).isEqualTo(['--master=foo', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldHandleKeepLogsOption() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit --master=foo --keep-logs artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.cleanUp()).isEqualTo(false)
        assertThat(container.arguments()).isEqualTo(['--master=foo', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldUseDefaultMasterUrl() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.arguments()).isEqualTo(['--master=spark://localhost:7077', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldUseDefaultMasterUrlForClientMode() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit --deploy-mode=client artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.arguments()).isEqualTo(['--master=spark://localhost:7077', '--deploy-mode=client', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldUseDefaultMasterUrlForClusterMode() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit --deploy-mode=cluster artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.arguments()).isEqualTo(['--master=spark://localhost:6066', '--deploy-mode=cluster', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }
}
