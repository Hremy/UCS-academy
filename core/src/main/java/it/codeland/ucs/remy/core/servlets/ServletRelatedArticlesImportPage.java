package it.codeland.ucs.remy.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Component(service = Servlet.class)
@SlingServletPaths(value = {"/bin/remy-ucs-related-articles-import-page"})
public class ServletRelatedArticlesImportPage extends SlingAllMethodsServlet {
    
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        Resource resource = request.getResource();
        ResourceResolver resourceResolver = resource.getResourceResolver();

        String out = "";

        try {

            final Map<String, RequestParameter[]> params = request.getRequestParameterMap();
            Iterator<Map.Entry<String, RequestParameter[]>> paramsIterator = params.entrySet().iterator();
            Map.Entry<String, RequestParameter[]> pairs = paramsIterator.next();
            RequestParameter[] parameterArray = pairs.getValue();
            RequestParameter fileCSV = parameterArray[0];

            int articlesStartFrom = 0;
            try {
                articlesStartFrom = Integer.parseInt(String.valueOf(paramsIterator.next().getValue()[0]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            int articlesEndAt = 0;
            try {
                articlesEndAt = Integer.parseInt(String.valueOf(paramsIterator.next().getValue()[0]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            boolean articlesContinueOnError = Boolean.parseBoolean(String.valueOf(paramsIterator.next().getValue()[0]));
            boolean articlesSkipEmptyRow = Boolean.parseBoolean(String.valueOf(paramsIterator.next().getValue()[0]));
            String articlesImportColumns = Arrays.toString(paramsIterator.next().getValue());
            if(articlesStartFrom < 0) articlesStartFrom = 0;
            if(articlesEndAt < 0) articlesEndAt = 0;

            out += ServletRelatedArticlesImport.importArticles(resource, resourceResolver, LOG, fileCSV, articlesStartFrom, articlesEndAt);

        }catch (Exception e) {
            out += "\nError: "+ e;
        }

        out = out.replace("\n", "<br>");
        response.setContentType("text/html");
        response.getWriter().write(out);
    }

}