package it.codeland.ucs.remy.core.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component(service = Servlet.class)
@SlingServletPaths(value = {"/bin/remy-ucs-related-articles-import-page"})
public class ServletRelatedArticlesImportPage extends SlingAllMethodsServlet {
    
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    @Reference
    protected ResourceResolverFactory resolverFactory;
    @SlingObject
    Resource resource;
    @SlingObject
    ResourceResolver resourceResolver;
    @Override

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        resource = request.getResource();
        resourceResolver = request.getResourceResolver();
        String out = "";
        try {
            final boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            final Map<String, RequestParameter[]> params = request.getRequestParameterMap();
            Iterator<Map.Entry<String, RequestParameter[]>> paramsIterator = params.entrySet().iterator();
            Map.Entry<String, RequestParameter[]> pairs = paramsIterator.next();
            RequestParameter[] parameterArray = pairs.getValue();
            RequestParameter fileCSV = parameterArray[0];
            String mimeType = FilenameUtils.getExtension(fileCSV.getFileName());
//            out += "\narticlesStartFrom: "+ request.getParameter("articlesStartFrom");
//            out += "\narticlesEndAt: "+ request.getParameter("articlesEndAt");
//            out += "\narticlesContinueOnError: "+ request.getParameter("articlesContinueOnError");
//            out += "\narticlesSkipEmptyRow: "+ request.getParameter("articlesSkipEmptyRow");
//            out += "\narticlesImportColumns: "+ request.getParameter("articlesImportColumns");
//            out += "\narticlesImportFrom: "+ request.getParameter("articlesImportFrom");
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
            String articlesImportFrom = Arrays.toString(paramsIterator.next().getValue());
            if(articlesStartFrom < 0) articlesStartFrom = 0;
            if(articlesEndAt < 0) articlesEndAt = 0;
//            out += "\n\narticlesStartFrom: "+ articlesStartFrom;
//            out += "\narticlesEndAt: "+ articlesEndAt;
//            out += "\narticlesContinueOnError: "+ articlesContinueOnError;
//            out += "\narticlesSkipEmptyRow: "+ articlesSkipEmptyRow;
//            out += "\narticlesImportColumns: "+ articlesImportColumns;
//            out += "\narticlesImportFrom: "+ articlesImportFrom;
            out += ServletRelatedArticlesImport.importArticles(resource, resourceResolver, LOG, fileCSV, articlesStartFrom, articlesEndAt, articlesContinueOnError, articlesSkipEmptyRow, articlesImportColumns, articlesImportFrom);
        }catch (Exception e) {
            out += "\nError: "+ e;
        }
        out = out.replace("\n", "<br>");
        response.setContentType("text/html");
        response.getWriter().write(out);
    }
}