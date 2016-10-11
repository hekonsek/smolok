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

import org.apache.commons.lang3.ArrayUtils

abstract class BaseCommand implements Command {

    private final String[] commandPrefix

    // Constructors

    BaseCommand(String[] commandPrefix) {
        this.commandPrefix = commandPrefix
    }

    BaseCommand(List<String> commandPrefix) {
        this.commandPrefix = commandPrefix.toArray(new String[0])
    }

    // Command operations

    @Override
    boolean supports(String... command) {
        if(commandPrefix.length > command.length) {
            return false
        }

        for(int i = 0; i < commandPrefix.length; i++) {
            if(commandPrefix[i] != command[i]) {
                return false
            }
        }

        true
    }

    @Override
    boolean helpRequested(String... command) {
        hasOption(command, 'help')
    }

    @Override
    String help() {
        'No help available for the command.'
    }

    @Override
    Optional<List<String>> supportedOptions() {
        Optional.empty()
    }

    protected validateOptions(String[] command) {
        if(supportedOptions().present) {
            def optionsPassed = command.findAll{ it.startsWith('--') }.collect{ it.substring(2) }
            optionsPassed.removeAll(supportedOptions().get())
            if(!optionsPassed.isEmpty()) {
                throw new IllegalArgumentException("Unsupported options used: ${optionsPassed.join(' ')}")
            }
        }
    }

    protected String option(String[] command, String optionName, String defaultValue) {
        def optionFound = command.find{ it.startsWith("--${optionName}=") }
        optionFound != null ? optionFound.replaceFirst(/--${optionName}=/, '') : defaultValue
    }

    protected Optional<String> option(String[] command, String optionName) {
        Optional.ofNullable(option(command, optionName, null))
    }

    protected Boolean hasOption(String[] command, String optionName) {
        command.find{ it.startsWith("--${optionName}") } ? true : false
    }

    protected String[] removeOption(String[] commands, String optionName) {
        commands.toList().findAll { !it.startsWith("--${optionName}") } as String[]
    }

    protected String[] putOptionAt(String[] commands, int index, String option) {
        ArrayUtils.add(commands, index, option)
    }

}