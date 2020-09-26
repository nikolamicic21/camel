/* Generated by camel build tools - do NOT edit this file! */
package org.apache.camel.component.spring.ws;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.spi.EndpointUriFactory;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
public class SpringWebserviceEndpointUriFactory extends org.apache.camel.support.component.EndpointUriFactorySupport implements EndpointUriFactory {

    private static final String BASE = ":type:lookupKey:webServiceEndpointUri";

    @Override
    public boolean isEnabled(String scheme) {
        return "spring-ws".equals(scheme);
    }

    @Override
    public String buildUri(String scheme, Map<String, Object> parameters) throws URISyntaxException {
        String syntax = scheme + BASE;
        String uri = syntax;

        Map<String, Object> copy = new HashMap<>(parameters);

        uri = buildPathParameter(syntax, uri, "type", null, false, copy);
        uri = buildPathParameter(syntax, uri, "lookupKey", null, false, copy);
        uri = buildPathParameter(syntax, uri, "webServiceEndpointUri", null, false, copy);
        uri = buildPathParameter(syntax, uri, "expression", null, false, copy);
        uri = buildQueryParameters(uri, copy);
        return uri;
    }
}

