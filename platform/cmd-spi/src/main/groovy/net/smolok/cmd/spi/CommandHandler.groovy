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
package net.smolok.cmd.spi

import groovy.transform.CompileStatic

/**
 * Provides set of operations required to by implemented by Smolok CMD commands.
 */
@CompileStatic
interface CommandHandler {

    /**
     * Indicates whether given command handler can execute command entered into the command line.
     *
     * @param command list of strings representing command typed into CMD.
     * @return whether given command implementation can handle entered command string.
     */
    boolean supports(String... command)

    /**
     * Executes the command. CommandHandler#supports() should be called before this method to ensure that given command
     * implementation can handle the given command text.
     *
     * @param outputSink Output of the command should be written to the given output sink.
     * @param commandId Indicates the particular instance of the command execution. Used to track command output and
     * state.
     * @param command CommandHandler text to be executed.
     */
    void handle(OutputSink outputSink, String commandId, String... command)

    boolean helpRequested(String... command)

    String help()

    Optional<List<String>> supportedOptions()

}