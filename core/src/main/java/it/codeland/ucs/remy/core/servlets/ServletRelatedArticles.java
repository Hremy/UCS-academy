package it.codeland.ucs.remy.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.http.util.TextUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Component(service = Servlet.class)
@SlingServletPaths(value = {"/bin/remy-ucs-related-articles"})
public class ServletRelatedArticles extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ServletRelatedArticles.class);

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @SlingObject
    private Resource resource;
    @SlingObject
    private ResourceResolver resourceResolver;


    public List<Map<String, String>> getListMapRelatedArticles() {
        return getListMapRelatedArticles("", resourceResolver, resource);
    }
    public List<Map<String, String>> getListMapRelatedArticles(String hashtag, ResourceResolver resourceResolver) {
        return getListMapRelatedArticles(hashtag, resourceResolver, resource);
    }
    public static List<Map<String, String>> getListMapRelatedArticles(String hashtag, ResourceResolver resourceResolver, Resource resource) {
        ArrayList<Map<String, String>> listMapRelatedArticles = new ArrayList<>();
        try {

            Page pageHome = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/us/en");
            Resource resourcePage = resourceResolver.getResource(pageHome.getPath());
            Node nodePage = resourcePage.adaptTo(Node.class);
            Node nodePageJCRContent = nodePage.getNode("jcr:content");
            Node nodeRelatedHashTags = nodePageJCRContent.hasNode("related-hashtags") ? nodePageJCRContent.getNode("related-hashtags") : null;

            int limit = 15;
            String order = "date_descending";
            String csvRelatedArticlesImportFile = "";
            if(nodeRelatedHashTags != null) {
                limit = Integer.parseInt(nodeRelatedHashTags.hasProperty("relatedArticlesMax") ? String.valueOf(nodeRelatedHashTags.getProperty("relatedArticlesMax").getValue()) : "20");
                order = nodeRelatedHashTags.hasProperty("relatedArticlesOrder") ? String.valueOf(nodeRelatedHashTags.getProperty("relatedArticlesOrder").getValue()) : "";
                csvRelatedArticlesImportFile = nodeRelatedHashTags.hasProperty("csvFileArticleRef") ? String.valueOf(nodeRelatedHashTags.getProperty("csvFileArticleRef").getValue()) : "";
            }

            Page pageMagazine = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/us/en/home-page/magazine");
            Iterator<Page> subpageMagazine = pageMagazine.listChildren();

            int i = 0;

            while(subpageMagazine.hasNext()) {

                Page pageMagazineChild = subpageMagazine.next();
                Resource resourcePageMagazine = resourceResolver.getResource(pageMagazineChild.getPath());
                Node nodePageMagazine = resourcePageMagazine.adaptTo(Node.class);
                Node nodePageMagazineJCRContent = nodePageMagazine.getNode("jcr:content");
                Node itemRelatedArticle1 = nodePageMagazineJCRContent.hasNode("related-articles") ? nodePageMagazineJCRContent.getNode("related-articles") : null;

                Map<String, String> mapRelatedArticle1 = new HashMap<>();

                mapRelatedArticle1.put("title", nodePageMagazineJCRContent.hasProperty("jcr:title") ? String.valueOf(nodePageMagazineJCRContent.getProperty("jcr:title").getValue()) : "");
                mapRelatedArticle1.put("text", nodePageMagazineJCRContent.hasProperty("jcr:description") ? String.valueOf(nodePageMagazineJCRContent.getProperty("jcr:description").getValue()) : "");
                mapRelatedArticle1.put("link", pageMagazineChild.getPath()+".html");
                mapRelatedArticle1.put("tags", nodePageMagazineJCRContent.hasProperty("cq:tags") ? Arrays.toString(nodePageMagazineJCRContent.getProperty("cq:tags").getValues()) : "");
                Value[] valueTags = nodePageMagazineJCRContent.hasProperty("cq:tags") ? nodePageMagazineJCRContent.getProperty("cq:tags").getValues() : new Value[] {};

                if(itemRelatedArticle1 != null) {
                    mapRelatedArticle1.put("hashtag", itemRelatedArticle1.hasProperty("hashtag") ? String.valueOf(itemRelatedArticle1.getProperty("hashtag").getValue()) : "");
                    mapRelatedArticle1.put("text", itemRelatedArticle1.hasProperty("text") ? String.valueOf(itemRelatedArticle1.getProperty("text").getValue()) : "");
                    mapRelatedArticle1.put("image", itemRelatedArticle1.hasProperty("imageArticleRef") ? String.valueOf(itemRelatedArticle1.getProperty("imageArticleRef").getValue()) : "");
                    mapRelatedArticle1.put("date", itemRelatedArticle1.hasProperty("date") ? String.valueOf(itemRelatedArticle1.getProperty("date").getDate().getTimeInMillis()) : "");
                    mapRelatedArticle1.put("target", itemRelatedArticle1.hasProperty("target") ? String.valueOf(itemRelatedArticle1.getProperty("target").getValue()) : "");
                }else {
                    mapRelatedArticle1.put("hashtag", hashtag);
                    mapRelatedArticle1.put("date", nodePageMagazineJCRContent.hasProperty("jcr:created") ? String.valueOf(nodePageMagazineJCRContent.getProperty("jcr:created").getValue()) : "");
                }

                String itemTag = ""+mapRelatedArticle1.get("tags");
                String itemHashtag = ""+mapRelatedArticle1.get("hashtag");
                if(itemHashtag.contains("/")) {
                    itemHashtag = itemHashtag.substring(itemHashtag.lastIndexOf("/") + 1);
                }
                if(itemHashtag.endsWith(":")) {
                    itemHashtag = itemHashtag.replace(":", "");
  
                }
                if(itemHashtag.contains(":")) {
                    itemHashtag = itemHashtag.substring(itemHashtag.lastIndexOf(":") + 1);
                }
                mapRelatedArticle1.put("hashtag", itemHashtag);

                ArrayList<String> tags = new ArrayList<String>();
                for (int ii = 0; ii < valueTags.length; ii++) {
                    Value value = valueTags[ii];
                    String itemHashtag1 = String.valueOf(value);
                    if (itemHashtag1.contains("/")) {
                        itemHashtag1 = itemHashtag1.substring(itemHashtag1.lastIndexOf("/") + 1);
                    }
                    if (itemHashtag1.endsWith(":")) {
                        itemHashtag1 = itemHashtag1.replace(":", "");
                    }
                    if (itemHashtag1.contains(":")) {
                        itemHashtag1 = itemHashtag1.substring(itemHashtag1.lastIndexOf(":") + 1);
                    }
                    tags.add(itemHashtag1);
                }
                if(!tags.contains(itemHashtag)) {
                    tags.add(itemHashtag);
                }
                mapRelatedArticle1.put("tags", Arrays.toString(tags.toArray()));

                String itemDate1 = mapRelatedArticle1.get("date");
                if(!TextUtils.isEmpty(itemDate1)) {
                    mapRelatedArticle1.put("dateMillis", itemDate1);
                    itemDate1 = new SimpleDateFormat("EEEE d MMM yyyy").format(new Date(Long.parseLong(itemDate1)));
                    mapRelatedArticle1.put("date", itemDate1);
                }

                if(hashtag.isEmpty() || hashtag.equalsIgnoreCase(itemHashtag) || itemTag.contains(hashtag)) {
                    if(i < limit) {
                        listMapRelatedArticles.add(mapRelatedArticle1);
                        i++;
                    }
                }

            }

            if((""+order).equalsIgnoreCase("date_descending")) {
                listMapRelatedArticles.sort(new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> map1, Map<String, String> map2) {
                        return new Date(Long.parseLong(map1.get("dateMillis"))).compareTo(new Date(Long.parseLong(map2.get("dateMillis"))));
                    }
                });
            }else if((""+order).equalsIgnoreCase("date_ascending")) {
                listMapRelatedArticles.sort(new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> map1, Map<String, String> map2) {
                        return new Date(Long.parseLong(map2.get("dateMillis"))).compareTo(new Date(Long.parseLong(map1.get("dateMillis"))));
                    }
                });
            }









            String query = "SELECT * FROM [cq:PageContent] AS a WHERE a.[jcr:content/cq:template]='/apps/academy-ucs-remy/templates/article-template' AND ISDESCENDANTNODE([/content/academy-ucs-remy/us/en]) ORDER BY a.[jcr:content/cq:lastModified] DESC";

            if((""+order).equalsIgnoreCase("date_descending")) {
                query = "SELECT * FROM [cq:PageContent] AS a WHERE a.[jcr:content/cq:template]='/apps/academy-ucs-remy/templates/article-template' AND ISDESCENDANTNODE([/content/academy-ucs-remy/us/en]) AND a.[jcr:content/cq:tags] LIKE '%"+hashtag+"%'  ORDER BY a.[jcr:content/cq:lastModified] DESC";

            }else {
                query = "SELECT * FROM [cq:PageContent] AS a WHERE a.[jcr:content/cq:template]='academy-ucs-remy/templates/article' AND ISDESCENDANTNODE([/content/academy-ucs-remy/us/en]) AND a.[jcr:content/cq:tags] LIKE '%"+hashtag+"%'  ORDER BY a.[jcr:content/cq:lastModified] ASC";
            }


            Iterator<Resource> resultQuery = resourceResolver.findResources(query, Query.JCR_SQL2);

            while(resultQuery.hasNext()) {
                Resource resource1 = resultQuery.next();
                Page pageMagazineChild = resourceResolver.adaptTo(PageManager.class).getPage(resource1.getPath());

                Resource resourcePageMagazine = resourceResolver.getResource(pageMagazineChild.getPath());
                Node nodePageMagazine = resourcePageMagazine.adaptTo(Node.class);
                Node nodePageMagazineJCRContent = nodePageMagazine.getNode("jcr:content");
                Node itemRelatedArticle1 = nodePageMagazineJCRContent.getNode("related-articles");


                Map<String, String> mapRelatedArticle1 = new HashMap<>();
                mapRelatedArticle1.put("title", nodePageMagazineJCRContent.hasProperty("title") ? String.valueOf(nodePageMagazineJCRContent.getProperty("title").getValue()) : "");

                mapRelatedArticle1.put("hashtag", itemRelatedArticle1.hasProperty("hashtag") ? String.valueOf(itemRelatedArticle1.getProperty("hashtag").getValue()) : "");
                mapRelatedArticle1.put("text", itemRelatedArticle1.hasProperty("text") ? String.valueOf(itemRelatedArticle1.getProperty("text").getValue()) : "");
                mapRelatedArticle1.put("link", pageMagazineChild.getPath()+".html");
                mapRelatedArticle1.put("image", itemRelatedArticle1.hasProperty("imageArticleRef") ? String.valueOf(itemRelatedArticle1.getProperty("imageArticleRef").getValue()) : "");
                mapRelatedArticle1.put("date", itemRelatedArticle1.hasProperty("date") ? String.valueOf(itemRelatedArticle1.getProperty("date").getDate().getTimeInMillis()) : "");
                mapRelatedArticle1.put("target", itemRelatedArticle1.hasProperty("target") ? String.valueOf(itemRelatedArticle1.getProperty("target").getValue()) : "");


                String itemHashtag1 = ""+mapRelatedArticle1.get("hashtag");
                if(itemHashtag1.contains("/")) {
                    itemHashtag1 = itemHashtag1.substring(itemHashtag1.lastIndexOf("/") + 1);
                }
                if(itemHashtag1.endsWith(":")) {
                    itemHashtag1 = itemHashtag1.replace(":", "");
                }
                if(itemHashtag1.contains(":")) {
                    itemHashtag1 = itemHashtag1.substring(itemHashtag1.lastIndexOf(":") + 1);
                }
                mapRelatedArticle1.put("hashtag", itemHashtag1);

                String itemDate1 = ""+mapRelatedArticle1.get("date");
                mapRelatedArticle1.put("dateMillis", itemDate1);
                itemDate1 = new SimpleDateFormat("EEEE d MMM yyyy").format(new Date(Long.parseLong(itemDate1)));
                mapRelatedArticle1.put("date", itemDate1);


                listMapRelatedArticles.add(mapRelatedArticle1);
            }

        }catch (Exception e){
            LOG.info("\n ERROR while getting Related Articles Items {} ", e);
        }

        return listMapRelatedArticles;
    }

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {

        String message = "";
        String error = "";
        JSONArray jsonData = new JSONArray();

        try {

            ResourceResolver resourceResolver = request.getResourceResolver();

            String hashtag = String.valueOf(request.getRequestParameter("hashtag"));

            List<Map<String, String>> listMapRelatedArticles = getListMapRelatedArticles(hashtag, resourceResolver);

            for(int i=0; i<listMapRelatedArticles.size(); i++) {

                Map<String, String> mapArticle = listMapRelatedArticles.get(i);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", mapArticle.get("title"));
                jsonObject.put("hashtag", mapArticle.get("hashtag"));
                jsonObject.put("text", mapArticle.get("text"));
                jsonObject.put("link", mapArticle.get("link"));
                jsonObject.put("image", mapArticle.get("image"));
                jsonObject.put("date", mapArticle.get("date"));
                jsonObject.put("target", mapArticle.get("target"));
                jsonData.put(jsonObject);
            }

            response.setStatus(200);

        } catch (Exception e) {
            LOG.info("\n ERROR IN SERVLET {} ", e.toString());

            response.setStatus(500);
            error += e;
        }

        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put("data", jsonData);
            jsonResponse.put("message", message);
            jsonResponse.put("error", error);
            jsonResponse.put("status", response.getStatus());
        } catch (Exception e) {
            LOG.info("\n ERROR IN SERVLET {} ", e.toString());
        }

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }

}