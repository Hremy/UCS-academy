package it.codeland.ucs.remy.core.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import it.codeland.ucs.remy.core.utils.Article;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {Servlet.class})
@SlingServletPaths({"/bin/remy-ucs-related-articles"})
public class ServletRelatedArticles extends SlingSafeMethodsServlet {

  private static final Logger LOG = LoggerFactory.getLogger(ServletRelatedArticles.class);
  
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
  
    JSONObject jsonResponse = new JSONObject();
  
    try {

      String path = request.getParameter("path");
      String hashtag = request.getParameter("hashtag").toLowerCase();

      int limit = 15;
      String order = "DESC";
      ValueMap config = getConfig(request.getResourceResolver(), path);
      if(config != null) {
        limit = config.get("max", limit);
        order = config.get("order", order);
      }

      StringBuilder query = new StringBuilder();
      query.append("SELECT * FROM [cq:Page] AS article ");
      query.append("WHERE ISDESCENDANTNODE(["+path+"]) ");
      query.append("AND article.[jcr:content/cq:template] = '/apps/academy-ucs-remy/templates/article-template'");
      query.append("AND article.[jcr:content/cq:tags] LIKE '%");
      query.append(hashtag);
      query.append("%' ");
      query.append("ORDER BY article.[jcr:content/date] ");
      query.append(order);
  
      ResourceResolver resourceResolver = request.getResourceResolver();
      Session session = resourceResolver.adaptTo(Session.class);

      QueryManager queryManager = session.getWorkspace().getQueryManager();
      Query sql2 = queryManager.createQuery(query.toString(), "JCR-SQL2");
      sql2.setLimit(limit);

      QueryResult result = sql2.execute();
      NodeIterator nodeIterator = result.getNodes();
      
      Gson gson = new Gson();
      JsonArray jsonArray = new JsonArray();

      ArrayList<Article> listArticles1 = new ArrayList<>();

      while (nodeIterator.hasNext()) {
        Node nodeArticle = nodeIterator.nextNode();
        Article article = getArticle(resourceResolver, nodeArticle);
        if (article != null) {
          listArticles1.add(article);
        }
      }

      ArrayList<Article> listArticlesNew = getCustomizedArticles(resourceResolver, listArticles1, path, hashtag, limit);

      for(Article article : listArticlesNew) {
        jsonArray.add(gson.toJson(article));
      }

      jsonResponse.put("data", jsonArray);

    } catch (Exception e) {
      LOG.info("\n ERROR IN ARTICLE SERVLET {} ", e.toString());
    }
    
    response.setContentType("application/json");
    response.getWriter().write(jsonResponse.toString());

  }
  
  public static Article getArticle(ResourceResolver resourceResolver, Node nodeArticle) {

    Article article = null;
    
    try {
    
      Node jcrContent = nodeArticle.getNode("jcr:content");
      String title = jcrContent.hasProperty("jcr:title") ? jcrContent.getProperty("jcr:title").getString() : "";
      String description = jcrContent.hasProperty("description") ? jcrContent.getProperty("description").getString() : "";
      String image = jcrContent.hasProperty("image") ? jcrContent.getProperty("image").getString() : "";
      Calendar calendar = jcrContent.hasProperty("date") ? jcrContent.getProperty("date").getDate() : null;
      Value[] cqTags = jcrContent.hasProperty("cq:tags") ? jcrContent.getProperty("cq:tags").getValues() : null;
      String path = nodeArticle.getPath();
      String date = "";
      
      if (calendar != null) {
        date = (new SimpleDateFormat("EEEE dd MMM yyyy")).format(calendar.getTime());
      }

      String tags = "[";
      String hashtag = "";
      if (cqTags != null && cqTags.length > 0) {
        for (int i = 0; i < cqTags.length; i++) {
          String cqTag = cqTags[i].getString();
          cqTag = cqTag.replace(":", "/");
          cqTag = "/content/cq:tags/" + cqTag;
          Resource resourceTag = resourceResolver.getResource(cqTag);
          if (resourceTag != null) {
            String tag = resourceTag.getValueMap().get("jcr:title", "");
            if (i == 0)
              hashtag = tag; 
            tags = tags + ((i > 0) ? ", " : "") + tag;
          } 
        }
      }
      tags += "]";

      article = new Article(title, description, image, date, hashtag, tags, path);
      
    } catch (Exception e) {
      LOG.info("\n ERROR IN MAGAZINE ARTICLE {} ", e.toString());
    } 
    
    return article;
  }

  public ArrayList<Article> getCustomizedArticles(ResourceResolver resourceResolver, ArrayList<Article> listArticles1, String path, String hashtag, int limit) {
    ArrayList<Article> listArticles = new ArrayList<>();
    Resource resource = resourceResolver.getResource(path + "/jcr:content/parsys/relatedhashtags/linksMapRelatedArticles");
    if(resource != null) {
      for (Iterator<Resource> iterator = resource.listChildren(); iterator.hasNext(); ) {
        Resource resourceItem = iterator.next();
        String articlePath = resourceItem.getValueMap().get("article", "");
        if(articlePath.length() != 0) {
          Resource resourceArticle = resourceResolver.getResource(articlePath);
          if(resourceArticle != null) {
            Node nodeArticle = resourceArticle.adaptTo(Node.class);
            Article article = getArticle(resourceResolver, nodeArticle);
            if(article != null && (""+article.getTags()).toLowerCase().contains((""+hashtag).toLowerCase())) {
              listArticles.add(article);
            }
          }
        }
      }
    }
    for(Article article : listArticles1) {
      if(listArticles.size() < limit && !listArticles.contains(article)) {
        listArticles.add(article);
      }
    }
    return listArticles;
  }

  public ValueMap getConfig(ResourceResolver resourceResolver, String path) {
    Resource resource = resourceResolver.getResource(path + "/jcr:content/parsys/relatedhashtags");
    return resource != null ? resource.getValueMap() : null;
  }

}