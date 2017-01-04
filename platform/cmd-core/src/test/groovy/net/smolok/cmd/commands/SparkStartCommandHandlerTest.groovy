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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import static org.mockito.BDDMockito.given
import static smolok.lib.common.Uuids.uuid
import static smolok.lib.process.ExecutorBasedProcessManager.command

class SparkStartCommandHandlerTest {
    def docker = Mockito.mock(Docker.class)

    def containerCaptor = ArgumentCaptor.forClass(Container.class)

    def commandId = uuid()

    @Test
    void shouldUseWorkerOpsParameter() {
        // Given
        given(docker.startService(containerCaptor.capture())).willReturn(null)
        def cmd = new SparkStartCommandHandler(docker)

        // When
        cmd.handle(null, commandId, command('spark start worker --workerOpts="foo bar" --localIP=127.0.0.1'))

        // Then
        def container = containerCaptor.value
        assertThat(container.environment()).containsEntry('SPARK_WORKER_OPTS', '"foo bar"')
        assertThat(container.environment()).containsEntry('SPARK_LOCAL_IP', '127.0.0.1')
    }
}
