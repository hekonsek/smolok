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
package net.smolok.service.binding

import org.apache.commons.lang3.builder.ReflectionToStringBuilder

import java.lang.reflect.Method

class OperationBinding {

    private final String service

    private final Method operationMethod

    private final List<?> arguments

    OperationBinding(String service, Method operationMethod, List<?> arguments) {
        this.service = service
        this.operationMethod = operationMethod
        this.arguments = arguments
    }

    String service() {
        service;
    }

    String operation() {
        operationMethod.name
    }

    public List<?> arguments() {
        return arguments;
    }

    public Method operationMethod() {
        return operationMethod;
    }

    @Override
    String toString() {
        ReflectionToStringBuilder.toString(this)
    }

}