package it.codeland.ucs.remy.core.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Component(service = { Servlet.class })
@SlingServletPaths(value = {"/bin/remy-ucs-related-articles-import"})
public class ServletRelatedArticlesImport extends SlingSafeMethodsServlet {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    @Reference
    protected ResourceResolverFactory resolverFactory;
    @SlingObject
    Resource resource;
    @SlingObject
    ResourceResolver resourceResolver;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        resource = request.getResource();
        resourceResolver = request.getResourceResolver();
        String out = "";
        try {
            out = importArticles(resource, resourceResolver, LOG, null);
        } catch (Exception e) {
            out += "\nError: "+ e;
        }
        response.setContentType("text/plain");
        response.getWriter().write(out);
    }

    private static int articlesStartFrom = 0;
    private static int articlesEndAt = 0;

    public static String importArticles(Resource resource, ResourceResolver resourceResolver, Logger LOG, RequestParameter fileCSV) throws Exception {

        Page pageHome = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/us/en");
        Resource resourcePage = resourceResolver.getResource(pageHome.getPath());
        Node nodePage = resourcePage.adaptTo(Node.class);
        Node nodePageJCRContent = nodePage.getNode("jcr:content");
        Node NodeContainer = nodePageJCRContent.getNode("parsys");
        Node nodeRelatedHashTags = NodeContainer.getNode("relatedhashtags");

        articlesStartFrom = nodeRelatedHashTags.hasProperty("articlesStartFrom") ?nodeRelatedHashTags.getProperty("articlesStartFrom").getDecimal().intValue() : articlesStartFrom;
        articlesEndAt = nodeRelatedHashTags.hasProperty("articlesEndAt") ? nodeRelatedHashTags.getProperty("articlesEndAt").getValue().getDecimal().intValue() : articlesEndAt;

        return importArticles(resource, resourceResolver, LOG, fileCSV, articlesStartFrom, articlesEndAt);
    }

    public static String importArticles(Resource resource, ResourceResolver resourceResolver, Logger LOG, RequestParameter fileCSV, int articlesStartFrom1, int articlesEndAt1) {

        articlesStartFrom = articlesStartFrom1;
        articlesEndAt = articlesEndAt1;

        Session session = resourceResolver.adaptTo(Session.class);
        String out = "";
        String csvRelatedArticlesImportFile = "/content/dam/academy-ucs-remy/csv-files/articles.csv";

        int imported = 0;
        int skipped = 0;
        long time1 = new Date().getTime();

        try {

            Page pageHome = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/us/en");
            Resource resourcePage = resourceResolver.getResource(pageHome.getPath());
            Node nodePage = resourcePage.adaptTo(Node.class);
            Node nodePageJCRContent = nodePage.getNode("jcr:content");
            Node NodeContainer = nodePageJCRContent.getNode("parsys");
            Node nodeRelatedHashTags = NodeContainer.getNode("relatedhashtags");
            csvRelatedArticlesImportFile = nodeRelatedHashTags.hasProperty("csvFileArticleRef") ? String.valueOf(nodeRelatedHashTags.getProperty("csvFileArticleRef").getValue()) : csvRelatedArticlesImportFile;
            Page pageMagazine = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/us/en/magazine");

            String path = pageMagazine.getPath();
            String template = "/apps/academy-ucs-remy/templates/article-template";
            String component = "/apps/academy-ucs-remy/components/structure/articlepage";

            ArrayList<HashMap<String, String>> listArticles = new ArrayList<>();

            try {
                listArticles.addAll(importCSV(csvRelatedArticlesImportFile, fileCSV, resource, resourceResolver, LOG));
            }catch (Exception e) {
                LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - CSV {} ", e.toString());
                out += "\nError: Articles CSV - CSV : "+ e;
            }

            for(int j=0; j<listArticles.size(); j++) {
                HashMap<String, String> hashMapArticle = listArticles.get(j);
                ArrayList<String> listTags = new ArrayList<>();
                try {
                    String tags = ""+hashMapArticle.get("tags");
                    tags = tags.replace("[", "");
                    tags = tags.replace("]", "");
                    tags = tags.replace(" ", "");
                    for(String tag : tags.split(",")) {
                        tag = tag.replace("[", "");
                        tag = tag.replace("]", "");
                        tag = tag.replace(" ", "");
                        listTags.add(""+tag);
                    }
                } catch (Exception e) {
                    LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - Tags {} ", e.toString());
                }

                Date date = new Date();
                try {
                    date = new SimpleDateFormat("EEEE d MMM yyyy").parse(hashMapArticle.get("date"));
                }catch (Exception e) {
                    LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - Date {} ", e.toString());
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                boolean articleExist = false;
                boolean articleUpdated = false;

                Page pageArticle;
                if (!pageMagazine.hasChild(hashMapArticle.get("name"))) {
                    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                    pageArticle = pageManager.create(path, hashMapArticle.get("name"), template, hashMapArticle.get("title"));
                } else {
                    articleExist = true;
                    pageArticle = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/us/en/magazine/" + hashMapArticle.get("name"));
                }

                Node nodeArticle = pageArticle.adaptTo(Node.class);
                Node nodeArticleJCR;

                if (pageArticle.hasContent()) {
                    nodeArticleJCR = pageArticle.getContentResource().adaptTo(Node.class);
                } else {
                    articleUpdated = true;
                    nodeArticleJCR = nodeArticle.addNode("jcr:content", "cq:PageContent");
                }

                nodeArticleJCR.setProperty("cq:template", template);
                nodeArticleJCR.setProperty("sling:resourceType", component);
                nodeArticleJCR.setProperty("jcr:title", hashMapArticle.get("title"));
                nodeArticleJCR.setProperty("pageTitle", hashMapArticle.get("title"));
                nodeArticleJCR.setProperty("navTitle", hashMapArticle.get("title"));
                nodeArticleJCR.setProperty("jcr:description", hashMapArticle.get("description"));
                nodeArticleJCR.setProperty("description", hashMapArticle.get("description"));
                nodeArticleJCR.setProperty("cq:tags", listTags.toArray(new String[0]));
                nodeArticleJCR.setProperty("date", calendar);
                nodeArticleJCR.setProperty("image", hashMapArticle.get("image"));

                double progress = ((j+1)*100.0) / listArticles.size();
                if (articleExist) {
                    if (articleUpdated) {
                        skipped++;
                        out += "\n" + (j + 1) + ". Article Existed (skipped) : " + hashMapArticle.get("name");
                    } else {
                        skipped++;
                        out += "\n" + (j + 1) + ". Article Existed (skipped) : " + hashMapArticle.get("name");
                    }
                } else {
                    imported++;
                    out += "\n" + (j + 1) + ". Article Imported : " + hashMapArticle.get("name");
                }

                nodeRelatedHashTags.setProperty("importProgress", (int) progress);
                nodeRelatedHashTags.setProperty("importProgressCurrent", (j+1));
                nodeRelatedHashTags.setProperty("importProgressTotal", listArticles.size());

                session.save();
                session.refresh(true);
            }
        } catch (Exception e) {
            LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV {} ", e.toString());
            out += "\nError: "+ e;
        }
        long time2 = new Date().getTime();
        long duration = (time2 - time1)/1000;

        out = "\n==== ARTICLES IMPORT LOGS ====\n* IMPORTED: "+ imported +"\n* SKIPPED: "+ skipped +"\n* DURATION: "+ duration+" Sec "+"\n\n"+ out;

        return out;
    }

    public static ArrayList<HashMap<String, String>> importCSV(String csvRelatedArticlesImportFile, RequestParameter fileCSV, Resource resource, ResourceResolver resourceResolver, Logger LOG) throws Exception {

        ArrayList<HashMap<String, String>> listArticles = new ArrayList<>();
        InputStream inputStream;

        if(fileCSV != null) {
            inputStream = fileCSV.getInputStream();
        }else {
            Resource res = resourceResolver.getResource(csvRelatedArticlesImportFile);
            Asset asset = res.adaptTo(Asset.class);
            Rendition rendition = asset.getOriginal();
            inputStream = rendition.adaptTo(InputStream.class);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        int i = 0;

        while ((line = br.readLine()) != null) {
            if (i != 0) {
                try {

                    if ((articlesStartFrom <= 0 || i >= articlesStartFrom) && (articlesEndAt <= 0 || i <= articlesEndAt)) {
                        line = line.replace(",", ",");
                        String[] arrayArticle = line.split(",");
                        HashMap<String, String> hashMapArticle = new HashMap<>();
                        hashMapArticle.put("name", ("" + arrayArticle[0]).toLowerCase().replace(" ", "-"));
                        hashMapArticle.put("title", arrayArticle[0]);
                        hashMapArticle.put("description", arrayArticle[1]);
                        hashMapArticle.put("tags", arrayArticle[2]);
                        hashMapArticle.put("date", arrayArticle[3]);
                        hashMapArticle.put("image", arrayArticle[4]);
                        listArticles.add(hashMapArticle);
                    }

                }catch(Exception e) {
                    LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - Import CSV "+ i +" {} ", e.toString());
                }
            }
            i++;
        }

        return listArticles;
    }

}