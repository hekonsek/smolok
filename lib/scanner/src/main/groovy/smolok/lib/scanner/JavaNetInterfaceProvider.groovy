/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package smolok.lib.scanner

import org.slf4j.LoggerFactory

import static java.net.NetworkInterface.networkInterfaces

class JavaNetInterfaceProvider implements InterfacesProvider {

    private final static LOG = LoggerFactory.getLogger(JavaNetInterfaceProvider.class)

    @Override
    List<NetworkInterface> interfaces() {
        getNetworkInterfaces().findAll {
            def ipv4Address = ipv4address(it)
            ipv4Address.present && ipv4Address.get().broadcast != null
        }.collect { java.net.NetworkInterface it ->
                    def ipv4Address = ipv4address(it)
                    LOG.debug("Checking ipv4Address " + ipv4Address)
                    def broadcast = ipv4Address.get().broadcast.hostName
                    new NetworkInterface(ipv4Address: ipv4Address.get(), broadcast: broadcast)
                }
    }

    private Optional<InterfaceAddress> ipv4address(java.net.NetworkInterface iface) {
        Optional.ofNullable(iface.interfaceAddresses.find{ Inet4Address.class.isAssignableFrom(it.getAddress().class) })
    }

}
