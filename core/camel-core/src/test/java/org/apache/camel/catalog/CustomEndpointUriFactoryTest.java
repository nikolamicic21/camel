/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package org.apache.camel.catalog;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.spi.EndpointUriFactory;
import org.apache.camel.support.component.EndpointUriFactorySupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CustomEndpointUriFactoryTest extends ContextTestSupport {

    @Test
    public void testCustomAssemble() throws Exception {
        EndpointUriFactory assembler = new MyAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "foo");
        params.put("amount", "123");
        params.put("port", 4444);
        params.put("verbose", true);

        String uri = assembler.buildUri("acme", params);
        Assertions.assertEquals("acme:foo:4444?amount=123&verbose=true", uri);
    }

    @Test
    public void testCustomUriFactoryRegistry() throws Exception {
        EndpointUriFactory assembler = new MyAssembler();
        context.getRegistry().bind("myAssembler", assembler);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "foo");
        params.put("amount", "123");
        params.put("port", 4444);
        params.put("verbose", true);

        assembler = context.adapt(ExtendedCamelContext.class).getEndpointUriFactory("acme");
        String uri = assembler.buildUri("acme", params);
        Assertions.assertEquals("acme:foo:4444?amount=123&verbose=true", uri);
    }

    @Test
    public void testCustomAssembleUnsorted() throws Exception {
        EndpointUriFactory assembler = new MyAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "foo");
        params.put("verbose", false);
        params.put("port", 4444);
        params.put("amount", "123");

        String uri = assembler.buildUri("acme", params);
        Assertions.assertEquals("acme:foo:4444?amount=123&verbose=false", uri);
    }

    @Test
    public void testCustomAssembleNoMandatory() throws Exception {
        EndpointUriFactory assembler = new MyAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("verbose", false);
        params.put("port", 4444);
        params.put("amount", "123");

        try {
            assembler.buildUri("acme", params);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Option name is required when creating endpoint uri with syntax acme:name:port",
                    e.getMessage());
        }
    }

    @Test
    public void testCustomAssembleDefault() throws Exception {
        EndpointUriFactory assembler = new MyAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "bar");
        params.put("verbose", false);
        params.put("amount", "123");

        String uri = assembler.buildUri("acme", params);
        Assertions.assertEquals("acme:bar?amount=123&verbose=false", uri);
    }

    @Test
    public void testCustomAssembleComplex() throws Exception {
        EndpointUriFactory assembler = new MySecondAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "bar");
        params.put("path", "moes");
        params.put("verbose", true);
        params.put("amount", "123");

        String uri = assembler.buildUri("acme2", params);
        Assertions.assertEquals("acme2:bar/moes?amount=123&verbose=true", uri);
    }

    @Test
    public void testCustomAssembleComplexPort() throws Exception {
        EndpointUriFactory assembler = new MySecondAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "bar");
        params.put("path", "moes");
        params.put("port", "4444");
        params.put("verbose", true);
        params.put("amount", "123");

        String uri = assembler.buildUri("acme2", params);
        Assertions.assertEquals("acme2:bar/moes:4444?amount=123&verbose=true", uri);
    }

    @Test
    public void testCustomAssembleComplexNoPath() throws Exception {
        EndpointUriFactory assembler = new MySecondAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "bar");
        params.put("port", "4444");
        params.put("verbose", true);
        params.put("amount", "123");

        String uri = assembler.buildUri("acme2", params);
        Assertions.assertEquals("acme2:bar:4444?amount=123&verbose=true", uri);
    }

    @Test
    public void testCustomAssembleComplexNoPathNoPort() throws Exception {
        EndpointUriFactory assembler = new MySecondAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "bar");
        params.put("verbose", true);
        params.put("amount", "123");

        String uri = assembler.buildUri("acme2", params);
        Assertions.assertEquals("acme2:bar?amount=123&verbose=true", uri);
    }

    @Test
    public void testJms() throws Exception {
        EndpointUriFactory assembler = new MyJmsAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("destinationName", "foo");
        params.put("destinationType", "topic");
        params.put("deliveryPersistent", true);

        String uri = assembler.buildUri("jms2", params);
        Assertions.assertEquals("jms2:topic:foo?deliveryPersistent=true", uri);
    }

    @Test
    public void testJmsMatchDefault() throws Exception {
        EndpointUriFactory assembler = new MyJmsAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("destinationName", "foo");
        params.put("destinationType", "queue");
        params.put("deliveryPersistent", true);

        String uri = assembler.buildUri("jms2", params);
        Assertions.assertEquals("jms2:queue:foo?deliveryPersistent=true", uri);
    }

    @Test
    public void testJmsNoDefault() throws Exception {
        EndpointUriFactory assembler = new MyJmsAssembler();
        assembler.setCamelContext(context);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("destinationName", "foo");
        params.put("deliveryPersistent", true);

        String uri = assembler.buildUri("jms2", params);
        Assertions.assertEquals("jms2:foo?deliveryPersistent=true", uri);
    }

    private class MyAssembler extends EndpointUriFactorySupport implements EndpointUriFactory {

        private static final String SYNTAX = "acme:name:port";

        @Override
        public boolean isEnabled(String scheme) {
            return "acme".equals(scheme);
        }

        @Override
        public String buildUri(String scheme, Map<String, Object> parameters)
                throws URISyntaxException {
            // begin from syntax
            String uri = SYNTAX;

            // append path parameters
            uri = buildPathParameter(SYNTAX, uri, "name", null, true, parameters);
            uri = buildPathParameter(SYNTAX, uri, "port", 8080, false, parameters);
            // append remainder parameters
            uri = buildQueryParameters(uri, parameters);

            return uri;
        }

    }

    private class MySecondAssembler extends EndpointUriFactorySupport implements EndpointUriFactory {

        private static final String SYNTAX = "acme2:name/path:port";

        @Override
        public boolean isEnabled(String scheme) {
            return "acme2".equals(scheme);
        }

        @Override
        public String buildUri(String scheme, Map<String, Object> parameters)
                throws URISyntaxException {
            // begin from syntax
            String uri = SYNTAX;

            // append path parameters
            uri = buildPathParameter(SYNTAX, uri, "name", null, true, parameters);
            uri = buildPathParameter(SYNTAX, uri, "path", null, false, parameters);
            uri = buildPathParameter(SYNTAX, uri, "port", 8080, false, parameters);
            // append remainder parameters
            uri = buildQueryParameters(uri, parameters);

            return uri;
        }

    }

    private class MyJmsAssembler extends EndpointUriFactorySupport implements EndpointUriFactory {

        private static final String SYNTAX = "jms2:destinationType:destinationName";

        @Override
        public boolean isEnabled(String scheme) {
            return "jms2".equals(scheme);
        }

        @Override
        public String buildUri(String scheme, Map<String, Object> parameters)
                throws URISyntaxException {

            String uri = SYNTAX;
            uri = buildPathParameter(SYNTAX, uri, "destinationType", "queue", false, parameters);
            uri = buildPathParameter(SYNTAX, uri, "destinationName", null, true, parameters);
            uri = buildQueryParameters(uri, parameters);

            return uri;
        }

    }

}
