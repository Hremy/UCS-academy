package it.codeland.ucs.remy.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.servlet.Servlet;
import java.io.IOException;


@Component(service = { Servlet.class })
@SlingServletPaths(value = {"/bin/remy-ucs-related-articles-import-progress"})
public class ServletRelatedArticlesImportProgress extends SlingSafeMethodsServlet {

    private final Logger LOG = LoggerFactory.getLogger(getClass());


    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {

        Resource resource = request.getResource();
        ResourceResolver resourceResolver = resource.getResourceResolver();

        JSONObject jsonData = new JSONObject();

        String progress = "0";
        String progressTotal = "0";
        String progressCurrent = "0";

        try {

            Page pageHome = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/us/en");
            Resource resourcePage = resourceResolver.getResource(pageHome.getPath());
            Node nodePage = resourcePage.adaptTo(Node.class);
            Node nodePageJCRContent = nodePage.getNode("jcr:content");
            Node NodeContainer = nodePageJCRContent.getNode("parsys");
            Node nodeRelatedHashTags = NodeContainer.getNode("relatedhashtags");

            progress = nodeRelatedHashTags.hasProperty("importProgress") ? String.valueOf(nodeRelatedHashTags.getProperty("importProgress").getValue()) : progress;
            progressTotal = nodeRelatedHashTags.hasProperty("importProgressTotal") ? String.valueOf(nodeRelatedHashTags.getProperty("importProgressTotal").getValue()) : progressTotal;
            progressCurrent = nodeRelatedHashTags.hasProperty("importProgressCurrent") ? String.valueOf(nodeRelatedHashTags.getProperty("importProgressCurrent").getValue()) : progressCurrent;

            jsonData.put("progress", progress);
            jsonData.put("total", progressTotal);
            jsonData.put("current", progressCurrent);

        }catch (Exception e) {
            LOG.info("\nRemyServletRelatedArticlesImportProgress: ERROR while getting importing Related Articles Progress {} ", e.toString());
        }

        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put("data", jsonData);
        } catch (Exception e) {
            LOG.info("\n ERROR IN SERVLET {} ", e.toString());
        }

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }

}