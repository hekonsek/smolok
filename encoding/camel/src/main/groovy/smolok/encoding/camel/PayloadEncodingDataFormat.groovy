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
package smolok.encoding.camel;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import smolok.encoding.spi.PayloadEncoding;

import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.commons.io.IOUtils.write;

class PayloadEncodingDataFormat implements DataFormat {

    private final PayloadEncoding payloadEncoding

    public PayloadEncodingDataFormat(PayloadEncoding payloadEncoding) {
        this.payloadEncoding = payloadEncoding;
    }

    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        byte[] encodedPayload = payloadEncoding.encode(exchange.getIn().getBody());
        write(encodedPayload, stream);
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        return payloadEncoding.decode(toByteArray(stream));
    }

}