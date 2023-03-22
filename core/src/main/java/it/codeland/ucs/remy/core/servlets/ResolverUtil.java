package it.codeland.ucs.remy.core.servlets;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

public class ResolverUtil {
  public static ResourceResolver newResolver(ResourceResolverFactory resourceResolverFactory) throws LoginException {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("sling.service.subservice", "remy-ucs");
    ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(paramMap);
    return resolver;
  }
}
