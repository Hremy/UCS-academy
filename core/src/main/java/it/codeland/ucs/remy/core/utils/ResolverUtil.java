package it.codeland.ucs.remy.core.utils;


import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.HashMap;
import java.util.Map;


public class ResolverUtil {

    public static ResourceResolver newResolver(ResourceResolverFactory resourceResolverFactory ) {
        ResourceResolver resolver = null;
        try {
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put( ResourceResolverFactory.SUBSERVICE, "remy-ucs-user" );
            if (resourceResolverFactory != null) {
                resolver = resourceResolverFactory.getServiceResourceResolver(paramMap);
            }
        } catch (LoginException ignore) {
        }
        return resolver;
    }

}
