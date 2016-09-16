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
package smolok.lib.process

import static org.apache.commons.io.IOUtils.readLines

class DefaultProcessManager extends ExecutorBasedProcessManager {

    @Override
    List<String> execute(Command command) {
        if(log.isDebugEnabled()) {
            log.debug('About to execute command:', command.command())
        }

        try {
            def commandSegments = command.command()

            def sudoPassword = command.sudoPassword()
            if(command.sudo()) {
                if(System.getProperty("user.name") == 'root' || sudoPassword == null || sudoPassword.isEmpty()) {
                    commandSegments.add(0, 'sudo')
                } else {
                    commandSegments = ['/bin/bash', '-c', "echo ${sudoPassword}| sudo -S ${commandSegments.join(' ')}"]
                }
            }

            def process = new ProcessBuilder().redirectErrorStream(true).command(commandSegments).start()
            def output = readLines(process.getInputStream())
            if(log.isDebugEnabled()) {
                log.debug('Output of the command {}: {}', commandSegments, output)
            }
            output
        } catch (IOException e) {
            throw new ProcessExecutionException(e)
        }
    }

}