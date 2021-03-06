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

import net.smolok.cmd.spi.OutputSink

class TestCommandHandler extends BaseCommandHandler {

    TestCommandHandler() {
        super('this', 'is', 'my', 'command')
    }

    @Override
    void handle(OutputSink outputSink, String commandId, String... command) {
        validateOptions(command)
    }

    @Override
    Optional<List<String>> supportedOptions() {
        Optional.of([])
    }

    @Override
    String help() {
        '''Use this command like that:

foo bar baz'''
    }

}
