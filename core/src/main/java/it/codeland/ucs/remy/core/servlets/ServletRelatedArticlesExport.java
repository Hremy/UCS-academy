package it.codeland.ucs.remy.core.servlets;

import com.day.cq.wcm.api.Page;
import com.google.gson.JsonObject;
import it.codeland.ucs.remy.core.models.OtherPage;
import it.codeland.ucs.remy.core.utils.Article;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = {Servlet.class})
@SlingServletResourceTypes(
        resourceTypes="academy-ucs-remy/components/structure/articlepage",
        methods= HttpConstants.METHOD_GET,
        selectors="export",
        extensions="json"
)
@ServiceDescription("Related Articles Export")
public class ServletRelatedArticlesExport extends SlingSafeMethodsServlet {
  
  private static final Logger LOG = LoggerFactory.getLogger(ServletRelatedArticlesExport.class);

  @Override
  protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
      
    Resource resource = request.getResource();
    ResourceResolver resourceResolver = resource.getResourceResolver();

    Node nodeArticle = resourceResolver.getResource(resource.getPath().replace("/jcr:content", "")).adaptTo(Node.class);

    Article article = ServletRelatedArticles.getArticle(resource.getResourceResolver(), nodeArticle);

    Page homePage = OtherPage.getHomePage(resourceResolver);

    ValueMap valueMapHashTag = resourceResolver.getResource(homePage.getPath() + "/jcr:content/parsys/relatedhashtags").getValueMap();

    JsonObject jsonResponse = new JsonObject();

    if(valueMapHashTag.get("title", false)) {
      jsonResponse.addProperty("title", article.getTitle());
    }

    if(valueMapHashTag.get("description", false)) {
      jsonResponse.addProperty("description", article.getDescription());
    }

    if(valueMapHashTag.get("image", false)) {
      jsonResponse.addProperty("image", article.getImage());
    }

    if(valueMapHashTag.get("date", false)) {
      jsonResponse.addProperty("date", article.getDate());
    }

    if(valueMapHashTag.get("tags", false)) {
      jsonResponse.addProperty("tags", article.getTags());
    }

    if(valueMapHashTag.get("path", false)) {
      jsonResponse.addProperty("path", article.getPath());
    }

    if(valueMapHashTag.get("link", false)) {
      jsonResponse.addProperty("link", article.getLink());
    }

    response.setContentType("application/json");
    response.getWriter().write(jsonResponse.toString());
  }

}
