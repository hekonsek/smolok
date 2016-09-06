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
package smolok.lib.common

class Reflections {

    private static final wrapperClasses = [:]
    static {
        wrapperClasses[int.class] = Integer.class
        wrapperClasses[long.class] = Long.class
        wrapperClasses[short.class] = Short.class
        wrapperClasses[byte.class] = Byte.class
        wrapperClasses[char.class] = Character.class
        wrapperClasses[float.class] = Float.class
        wrapperClasses[double.class] = Double.class
    }

    private static final wrappersOf = [:]
    static {
        wrappersOf[Integer.class] = int.class
        wrappersOf[Long.class] = long.class
        wrappersOf[Short.class] = short.class
        wrappersOf[Byte.class] = byte.class
        wrappersOf[Character.class] = char.class
        wrappersOf[Float.class] = float.class
        wrappersOf[Double.class] = double.class
    }

    static boolean wrappableAwareInstanceOf(Class<?> type, Class<?> instanceOf) {
        if (instanceOf.isAssignableFrom(type)) {
            return true;
        } else {
            Class<?> resolvedType = wrapperClasses.get(type)
            if(resolvedType == null) {
                resolvedType = wrappersOf[type]
            }
            instanceOf.isAssignableFrom(resolvedType)
        }
    }

    static boolean isNumber(Class<?> type) {
        wrapperClasses.containsKey(type) || wrapperClasses.containsValue(type)
    }

    static boolean isJavaLibraryType(Class<?> type) {
        isNumber(type) || type == String.class || type == Date.class;
    }

    static boolean isContainer(Class<?> type) {
        Map.isAssignableFrom(type) || Collection.isAssignableFrom(type)
    }

    static boolean isPojo(Class<?> type) {
        type != Object.class && !isJavaLibraryType(type) && !isContainer(type)
    }

}
