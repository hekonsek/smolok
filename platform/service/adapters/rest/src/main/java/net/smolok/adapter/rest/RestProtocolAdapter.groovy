/**
 * Licensed to the Rhiot under one or more
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
package net.smolok.adapter.rest

import org.apache.camel.builder.RouteBuilder
import org.slf4j.LoggerFactory
import smolok.encoding.spi.PayloadEncoding

import static org.apache.camel.Exchange.*
import static org.apache.commons.lang3.StringUtils.removeEnd

public class RestProtocolAdapter extends RouteBuilder {

    private static final LOG = LoggerFactory.getLogger(RestProtocolAdapter.class)

    // Constants

    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    private final PayloadEncoding encoding

    // Members

    private final int port;

    private final String contentType;

    // Constructors

    RestProtocolAdapter(PayloadEncoding encoding, int port, String contentType) {
        this.encoding = encoding
        this.port = port
        this.contentType = contentType
    }

    // Routes

    @Override
    public void configure() throws Exception {
        LOG.debug("Started REST data stream source at port {}.", port);

        from("netty4-http:http://0.0.0.0:" + port + "/?matchOnUriPrefix=true&httpMethodRestrict=OPTIONS,GET,POST,PUT,DELETE").
                choice().
                    when(header(HTTP_METHOD).isEqualTo("OPTIONS")).setBody().constant("").endChoice().
                otherwise().
                    setHeader(CONTENT_TYPE).constant(contentType).
                    process {
                        String requestUri = it.getIn().getHeader(HTTP_URI, String.class);
                        LOG.debug("Processing request URI: {}", requestUri);
                        String trimmedUri = removeEnd(requestUri, "/");
                        LOG.debug("Trimmed request URI: {}", trimmedUri);
                        String busChannel = trimmedUri.substring(1).replaceAll("\\/", ".");
                        if(it.in.getBody(byte[].class).length > 0) {
                            it.in.body = encoding.decode(it.in.getBody(byte[].class))
                        } else {
                            it.in.body = null
                        }
                        it.setProperty("target", "eventbus:" + busChannel);
                    }.toD('${property.target}').endChoice().
                end().
                process {
                    it.getIn().setHeader("Access-Control-Allow-Origin", "*");
                    it.getIn().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
                    it.in.body = encoding.encode(it.in.body)
                }
    }

}