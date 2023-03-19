package it.codeland.ucs.remy.core.servlets;


import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.HashMap;
import java.util.Map;


public class ResolverUtil {

    public static ResourceResolver newResolver(ResourceResolverFactory resourceResolverFactory ) throws LoginException {
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put( ResourceResolverFactory.SUBSERVICE, "remy-ucs" );
        ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(paramMap);
        return resolver;
    }

}