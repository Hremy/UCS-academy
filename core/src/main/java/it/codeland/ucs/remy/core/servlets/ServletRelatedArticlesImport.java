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
        // out = out.replace("\n", "<br>");
        response.setContentType("text/plain");
        response.getWriter().write(out);
    }
    private static int articlesStartFrom = 0;
    private static int articlesEndAt = 0;
    private static boolean articlesContinueOnError = true;
    private static boolean articlesSkipEmptyRow = true;
    private static String articlesImportFrom = "csv";
    private static String articlesImportColumns = "";
    public static String importArticles(Resource resource, ResourceResolver resourceResolver, Logger LOG, RequestParameter fileCSV) throws Exception {
        Page pageHome = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/us/en");
        Resource resourcePage = resourceResolver.getResource(pageHome.getPath());
        Node nodePage = resourcePage.adaptTo(Node.class);
        Node nodePageJCRContent = nodePage.getNode("jcr:content");
        Node nodeRelatedHashTags = nodePageJCRContent.getNode("related-hashtags");
        articlesStartFrom = nodeRelatedHashTags.hasProperty("articlesStartFrom") ?nodeRelatedHashTags.getProperty("articlesStartFrom").getDecimal().intValue() : articlesStartFrom;
        articlesEndAt = nodeRelatedHashTags.hasProperty("articlesEndAt") ? nodeRelatedHashTags.getProperty("articlesEndAt").getValue().getDecimal().intValue() : articlesEndAt;
        articlesContinueOnError = nodeRelatedHashTags.hasProperty("articlesContinueOnError") ? nodeRelatedHashTags.getProperty("articlesContinueOnError").getBoolean() : articlesContinueOnError;
        articlesSkipEmptyRow = nodeRelatedHashTags.hasProperty("articlesSkipEmptyRow") ? nodeRelatedHashTags.getProperty("articlesSkipEmptyRow").getBoolean() : articlesSkipEmptyRow;
        articlesImportColumns = nodeRelatedHashTags.hasProperty("articlesImportColumns") ? Arrays.toString(nodeRelatedHashTags.getProperty("articlesImportColumns").getValues()) : articlesImportColumns;
        articlesImportColumns = nodeRelatedHashTags.hasProperty("articlesImportFrom") ? Arrays.toString(nodeRelatedHashTags.getProperty("articlesImportFrom").getValues()) : articlesImportFrom;
        return importArticles(resource, resourceResolver, LOG, fileCSV, articlesStartFrom, articlesEndAt, articlesContinueOnError, articlesSkipEmptyRow, articlesImportColumns, articlesImportFrom);
    }

    public static String importArticles(Resource resource, ResourceResolver resourceResolver, Logger LOG, RequestParameter fileCSV, int articlesStartFrom1, int articlesEndAt1, boolean articlesContinueOnError1, boolean articlesSkipEmptyRow1, String articlesImportColumns1, String articlesImportFrom1) {
        articlesStartFrom = articlesStartFrom1;
        articlesEndAt = articlesEndAt1;
        articlesContinueOnError = articlesContinueOnError1;
        articlesSkipEmptyRow = articlesSkipEmptyRow1;
        articlesImportColumns = articlesImportColumns1;
        Session session = resourceResolver.adaptTo(Session.class);
        String out = "";
        String csvRelatedArticlesImportFile = "/content/dam/academy-ucs-remy/related-articles-import-csv/related-articles-import.csv";
        String sqlRelatedArticlesImportFile = "/content/dam/academy-ucs-remy/related-articles-import-csv/related-articles-import.sql";
        String jsonRelatedArticlesImportFile = "/content/dam/academy-ucs-remy/related-articles-import-csv/related-articles-import.json";
        String xmlRelatedArticlesImportFile = "/content/dam/academy-ucs-remy/related-articles-import-csv/related-articles-import.sql";
        String txtRelatedArticlesImportFile = "/content/dam/academy-ucs-remy/related-articles-import-csv/related-articles-import.txt";
        int imported = 0;
        int skipped = 0;
        int countArticlesEmpty = 0;
        long time1 = new Date().getTime();
        try {
            Page pageHome = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/us/en");
            Resource resourcePage = resourceResolver.getResource(pageHome.getPath());
            Node nodePage = resourcePage.adaptTo(Node.class);
            Node nodePageJCRContent = nodePage.getNode("jcr:content");
            Node nodeRelatedHashTags = nodePageJCRContent.getNode("related-hashtags");
            csvRelatedArticlesImportFile = nodeRelatedHashTags.hasProperty("csvFileArticleRef") ? String.valueOf(nodeRelatedHashTags.getProperty("csvFileArticleRef").getValue()) : csvRelatedArticlesImportFile;
            sqlRelatedArticlesImportFile = nodeRelatedHashTags.hasProperty("sqlFileArticleRef") ? String.valueOf(nodeRelatedHashTags.getProperty("sqlFileArticleRef").getValue()) : sqlRelatedArticlesImportFile;
            jsonRelatedArticlesImportFile = nodeRelatedHashTags.hasProperty("jsonFileArticleRef") ? String.valueOf(nodeRelatedHashTags.getProperty("jsonFileArticleRef").getValue()) : jsonRelatedArticlesImportFile;
            xmlRelatedArticlesImportFile = nodeRelatedHashTags.hasProperty("xmlFileArticleRef") ? String.valueOf(nodeRelatedHashTags.getProperty("xmlFileArticleRef").getValue()) : xmlRelatedArticlesImportFile;
            txtRelatedArticlesImportFile = nodeRelatedHashTags.hasProperty("txtFileArticleRef") ? String.valueOf(nodeRelatedHashTags.getProperty("txtFileArticleRef").getValue()) : txtRelatedArticlesImportFile;
            Page pageMagazine = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/magazine");
            String path = pageMagazine.getPath();
            String template = "/apps/academy-ucs-remy/templates/article";
            String component = "/apps/academy-ucs-remy/components/structure/article";
            ArrayList<HashMap<String, String>> listArticles = new ArrayList<>();
            try {
                if((""+articlesImportFrom).contains("csv")) {
                    listArticles.addAll(importCSV(csvRelatedArticlesImportFile, fileCSV, resource, resourceResolver, LOG));
                }
            }catch (Exception e) {
                LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - CSV {} ", e.toString());
                out += "\nError: Articles CSV - CSV : "+ e;
            }
            try {
                if((""+articlesImportFrom).contains("sql")) {
                    listArticles.addAll(importSQL(sqlRelatedArticlesImportFile, resource, resourceResolver, LOG));
                }
            }catch (Exception e) {
                LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - SQL {} ", e.toString());
                out += "\nError: Articles CSV - SQL : "+ e;
            }
            try {
                if((""+articlesImportFrom).contains("json")) {
                    listArticles.addAll(importJSON(jsonRelatedArticlesImportFile, resource, resourceResolver, LOG));
                }
            }catch (Exception e) {
                LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - JSON {} ", e.toString());
                out += "\nError: Articles CSV - JSON : "+ e;
            }
            try {
                if((""+articlesImportFrom).contains("xml")) {
                    listArticles.addAll(importXML(xmlRelatedArticlesImportFile, resource, resourceResolver, LOG));
                }
            }catch (Exception e) {
                LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - XML {} ", e.toString());
                out += "\nError: Articles CSV - XML : "+ e;
            }
            try {
                if((""+articlesImportFrom).contains("txt")) {
                    listArticles.addAll(importTXT(txtRelatedArticlesImportFile, resource, resourceResolver, LOG));
                }
            }catch (Exception e) {
                LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - TXT {} ", e.toString());
                out += "\nError: Articles CSV - TXT : "+ e;
            }
            for(int j=0; j<listArticles.size(); j++) {
                HashMap<String, String> hashMapArticle = listArticles.get(j);
                String hashtag = "";
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
                        hashtag = tag;
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
                boolean articleEmpty = false;
                if(hashMapArticle.get("name").isEmpty() || hashMapArticle.get("title").isEmpty() || hashMapArticle.get("tags").isEmpty() || hashMapArticle.get("image").isEmpty() || hashMapArticle.get("date").isEmpty()) {
                    articleEmpty = true;
                }
                boolean articleExist = false;
                boolean articleUpdated = false;
                Page pageArticle;
                if (!pageMagazine.hasChild(hashMapArticle.get("name"))) {
                    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                    pageArticle = pageManager.create(path, hashMapArticle.get("name"), template, hashMapArticle.get("title"));
                } else {
                    articleExist = true;
                    pageArticle = resourceResolver.adaptTo(PageManager.class).getPage("/content/academy-ucs-remy/magazine/" + hashMapArticle.get("name"));
                }
                if(!articlesSkipEmptyRow || !articleEmpty) {
                    Node nodeArticle = pageArticle.adaptTo(Node.class);
                    Node nodeArticleJCR;
                    if (pageArticle.hasContent()) {
                        nodeArticleJCR = pageArticle.getContentResource().adaptTo(Node.class);
                    } else {
                        articleUpdated = true;
                        nodeArticleJCR = nodeArticle.addNode("jcr:content", "cq:PageContent");
                    }
                    nodeArticleJCR.setProperty("sling:resourceType", component);
                    nodeArticleJCR.setProperty("pageTitle", hashMapArticle.get("title"));
                    nodeArticleJCR.setProperty("navTitle", hashMapArticle.get("name"));
                    nodeArticleJCR.setProperty("jcr:title", hashMapArticle.get("title"));
                    nodeArticleJCR.setProperty("cq:tags", listTags.toArray(new String[0]));
                    nodeArticleJCR.setProperty("cq:template", template);
                    nodeArticleJCR.setProperty("sling:resourceType", component);
                    Node nodeArticleJCRRelatedArticles;
                    if (nodeArticleJCR.hasNode("related-articles")) {
                        nodeArticleJCRRelatedArticles = nodeArticleJCR.getNode("related-articles");
                    } else {
                        nodeArticleJCRRelatedArticles = nodeArticleJCR.addNode("related-articles");
                    }
                    nodeArticleJCRRelatedArticles.setProperty("sling:resourceType", "academy-ucs-remy/components/structure/relatedarticles");
                    nodeArticleJCRRelatedArticles.setProperty("category", "OUR PEOPLE");
                    nodeArticleJCRRelatedArticles.setProperty("date", calendar);
                    nodeArticleJCRRelatedArticles.setProperty("name", hashMapArticle.get("name"));
                    nodeArticleJCRRelatedArticles.setProperty("text", hashMapArticle.get("text"));
                    nodeArticleJCRRelatedArticles.setProperty("summary", hashMapArticle.get("description"));
                    nodeArticleJCRRelatedArticles.setProperty("hashtag", hashtag);
                    nodeArticleJCRRelatedArticles.setProperty("imageArticleRef", hashMapArticle.get("image"));
                    nodeArticleJCRRelatedArticles.setProperty("target", hashMapArticle.get("target"));
                    nodeArticleJCRRelatedArticles.setProperty("quote", "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam quis nostrud</p>");
                    nodeArticleJCRRelatedArticles.setProperty("signature", "jHrem");
                    nodeArticleJCRRelatedArticles.setProperty("description", "sed do eiusmod tempor incididunt ut labore Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem: Ut enim ad minima veniam, quis nostrum exercitationem Quis autem vel eum iure reprehenderit qui in ea voluptate");
                    nodeArticleJCRRelatedArticles.setProperty("textIsRich", "[true,true,true]");
                }
                double progress = ((j+1)*100.0) / listArticles.size();
                if(articleEmpty) {
                    countArticlesEmpty++;
                    out +=  "\n"+ (j+1) +". Article Empty : " + hashMapArticle.get("name");
                }else {
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
        if(articlesSkipEmptyRow)  {
            out = "\n==== ARTICLES IMPORT LOGS ====\n* IMPORTED: "+ imported +"\n* SKIPPED: "+ skipped +"\n* EMPTY: "+ countArticlesEmpty +"\n* DURATION: "+ duration+" Sec "+"\n\n"+ out;
        }else {
            // out = "\n==== ARTICLES IMPORT LOGS ====\n* IMPORTED: "+ imported +"\n* SKIPPED: "+ skipped +"\n* DURATION: "+ duration+" Sec "+"\n\n"+ out;
            out = "\n==== ARTICLES IMPORT LOGS ====\n* IMPORTED: "+ imported +"\n* SKIPPED: "+ skipped +"\n* EMPTY: "+ countArticlesEmpty +"\n* DURATION: "+ duration+" Sec "+"\n\n"+ out;
        }
        return out;
    }

    public static ArrayList<HashMap<String, String>> importCSV(String csvRelatedArticlesImportFile, RequestParameter fileCSV, Resource resource, ResourceResolver resourceResolver, Logger LOG) throws Exception {
        String[] arrayArticleHeader = new String[0];
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
        boolean isError = false;
        String line = "";
        int i = 0;
        while ((line = br.readLine()) != null) {
            if(i == 0) {
                arrayArticleHeader = line.split(",");
            }else {
                try {
                    if(articlesContinueOnError || !isError) {
                        if ((articlesStartFrom <= 0 || i >= articlesStartFrom) && (articlesEndAt <= 0 || i <= articlesEndAt)) {
                            line = line.replace(",", ",");
                            String[] arrayArticle = line.split(",");
                            HashMap<String, String> hashMapArticle = new HashMap<>();
                            hashMapArticle.put("name", ("" + arrayArticle[0]).toLowerCase().replace(" ", "-"));
                            hashMapArticle.put("title", arrayArticle[0]);
                            hashMapArticle.put("text", arrayArticle[1]);
                            hashMapArticle.put("description", arrayArticle[2]);
                            hashMapArticle.put("tags", arrayArticle[3]);
                            hashMapArticle.put("image", arrayArticle[4]);
                            hashMapArticle.put("date", arrayArticle[5]);
                            hashMapArticle.put("target", arrayArticle[6]);
                            listArticles.add(hashMapArticle);
                        }
                    }
                }catch(Exception e) {
                    LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - Import CSV "+ i +" {} ", e.toString());
                    isError = true;
                }
            }
            i++;
        }
        return listArticles;
    }

    public static ArrayList<HashMap<String, String>> importSQL(String sqlRelatedArticlesImportFile, Resource resource, ResourceResolver resourceResolver, Logger LOG) throws Exception {
        String[] arrayArticleHeader = new String[0];
        ArrayList<HashMap<String, String>> listArticles = new ArrayList<>();
        Resource res = resourceResolver.getResource(sqlRelatedArticlesImportFile);
        Asset asset = res.adaptTo(Asset.class);
        Rendition rendition = asset.getOriginal();
        InputStream inputStream = rendition.adaptTo(InputStream.class);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        boolean isError = false;
        String line = "";
        int i = 0;
        while ((line = br.readLine()) != null) {
            if(i == 0) {
                arrayArticleHeader = line.split(",");
            }else {
                try {
                    if(articlesContinueOnError || !isError) {
                        if ((articlesStartFrom <= 0 || i >= articlesStartFrom) && (articlesEndAt <= 0 || i <= articlesEndAt)) {
                            line = line.replace(",", ",");
                            String[] arrayArticle = line.split(",");
                            HashMap<String, String> hashMapArticle = new HashMap<>();
                            hashMapArticle.put("name", ("" + arrayArticle[0]).toLowerCase().replace(" ", "-"));
                            hashMapArticle.put("title", arrayArticle[0]);
                            hashMapArticle.put("text", arrayArticle[1]);
                            hashMapArticle.put("description", arrayArticle[2]);
                            hashMapArticle.put("tags", arrayArticle[3]);
                            hashMapArticle.put("image", arrayArticle[4]);
                            hashMapArticle.put("date", arrayArticle[5]);
                            hashMapArticle.put("target", arrayArticle[6]);
                            if (articlesSkipEmptyRow) {
                                if (!hashMapArticle.get("name").isEmpty() && !hashMapArticle.get("title").isEmpty() && !hashMapArticle.get("tags").isEmpty() && !hashMapArticle.get("image").isEmpty() && !hashMapArticle.get("date").isEmpty()) {
                                    listArticles.add(hashMapArticle);
                                }
                            } else {
                                listArticles.add(hashMapArticle);
                            }
                        }
                    }
                }catch(Exception e) {
                    LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - Import SQL "+ i +" {} ", e.toString());
                    isError = true;
                }
            }
            i++;
        }
        return listArticles;
    }

    public static ArrayList<HashMap<String, String>> importJSON(String jsonRelatedArticlesImportFile, Resource resource, ResourceResolver resourceResolver, Logger LOG) throws Exception {
        String[] arrayArticleHeader = new String[0];
        ArrayList<HashMap<String, String>> listArticles = new ArrayList<>();
        Resource res = resourceResolver.getResource(jsonRelatedArticlesImportFile);
        Asset asset = res.adaptTo(Asset.class);
        Rendition rendition = asset.getOriginal();
        InputStream inputStream = rendition.adaptTo(InputStream.class);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String json = "";
        json.replace("", "");
        boolean isError = false;
        String line = "";
        int i = 0;
        while ((line = br.readLine()) != null) {
            if(i == 0) {
                arrayArticleHeader = line.split(",");
            }else {
                try {
                    if(articlesContinueOnError || !isError) {
                        if ((articlesStartFrom <= 0 || i >= articlesStartFrom) && (articlesEndAt <= 0 || i <= articlesEndAt)) {
                            line = line.replace(",", ",");
                            String[] arrayArticle = line.split(",");
                            HashMap<String, String> hashMapArticle = new HashMap<>();
                            hashMapArticle.put("name", ("" + arrayArticle[0]).toLowerCase().replace(" ", "-"));
                            hashMapArticle.put("title", arrayArticle[0]);
                            hashMapArticle.put("text", arrayArticle[1]);
                            hashMapArticle.put("description", arrayArticle[2]);
                            hashMapArticle.put("tags", arrayArticle[3]);
                            hashMapArticle.put("image", arrayArticle[4]);
                            hashMapArticle.put("date", arrayArticle[5]);
                            hashMapArticle.put("target", arrayArticle[6]);
                            if (articlesSkipEmptyRow) {
                                if (!hashMapArticle.get("name").isEmpty() && !hashMapArticle.get("title").isEmpty() && !hashMapArticle.get("tags").isEmpty() && !hashMapArticle.get("image").isEmpty() && !hashMapArticle.get("date").isEmpty()) {
                                    listArticles.add(hashMapArticle);
                                }
                            } else {
                                listArticles.add(hashMapArticle);
                            }
                        }
                    }
                }catch(Exception e) {
                    LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - Import JSON "+ i +" {} ", e.toString());
                    isError = true;
                }
            }
            i++;
        }
        return listArticles;
    }

    public static ArrayList<HashMap<String, String>> importXML(String xmlRelatedArticlesImportFile, Resource resource, ResourceResolver resourceResolver, Logger LOG) throws Exception {
        String[] arrayArticleHeader = new String[0];
        ArrayList<HashMap<String, String>> listArticles = new ArrayList<>();
        Resource res = resourceResolver.getResource(xmlRelatedArticlesImportFile);
        Asset asset = res.adaptTo(Asset.class);
        Rendition rendition = asset.getOriginal();
        InputStream inputStream = rendition.adaptTo(InputStream.class);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        boolean isError = false;
        String line = "";
        int i = 0;
        while ((line = br.readLine()) != null) {
            if(i == 0) {
                arrayArticleHeader = line.split(",");
            }else {
                try {
                    if(articlesContinueOnError || !isError) {
                        if ((articlesStartFrom <= 0 || i >= articlesStartFrom) && (articlesEndAt <= 0 || i <= articlesEndAt)) {
                            line = line.replace(",", ",");
                            String[] arrayArticle = line.split(",");
                            HashMap<String, String> hashMapArticle = new HashMap<>();
                            hashMapArticle.put("name", ("" + arrayArticle[0]).toLowerCase().replace(" ", "-"));
                            hashMapArticle.put("title", arrayArticle[0]);
                            hashMapArticle.put("text", arrayArticle[1]);
                            hashMapArticle.put("description", arrayArticle[2]);
                            hashMapArticle.put("tags", arrayArticle[3]);
                            hashMapArticle.put("image", arrayArticle[4]);
                            hashMapArticle.put("date", arrayArticle[5]);
                            hashMapArticle.put("target", arrayArticle[6]);
                            if (articlesSkipEmptyRow) {
                                if (!hashMapArticle.get("name").isEmpty() && !hashMapArticle.get("title").isEmpty() && !hashMapArticle.get("tags").isEmpty() && !hashMapArticle.get("image").isEmpty() && !hashMapArticle.get("date").isEmpty()) {
                                    listArticles.add(hashMapArticle);
                                }
                            } else {
                                listArticles.add(hashMapArticle);
                            }
                        }
                    }
                }catch(Exception e) {
                    LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - Import XML "+ i +" {} ", e.toString());
                    isError = true;
                }
            }
            i++;
        }
        return listArticles;
    }

    public static ArrayList<HashMap<String, String>> importTXT(String txtRelatedArticlesImportFile, Resource resource, ResourceResolver resourceResolver, Logger LOG) throws Exception {
        String[] arrayArticleHeader = new String[0];
        ArrayList<HashMap<String, String>> listArticles = new ArrayList<>();
        Resource res = resourceResolver.getResource(txtRelatedArticlesImportFile);
        Asset asset = res.adaptTo(Asset.class);
        Rendition rendition = asset.getOriginal();
        InputStream inputStream = rendition.adaptTo(InputStream.class);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        boolean isError = false;
        String line = "";
        int i = 0;
        while ((line = br.readLine()) != null) {
            if(i == 0) {
                arrayArticleHeader = line.split(",");
            }else {
                try {
                    if(articlesContinueOnError || !isError) {
                        if ((articlesStartFrom <= 0 || i >= articlesStartFrom) && (articlesEndAt <= 0 || i <= articlesEndAt)) {
                            line = line.replace(",", ",");
                            String[] arrayArticle = line.split(",");
                            HashMap<String, String> hashMapArticle = new HashMap<>();
                            hashMapArticle.put("name", ("" + arrayArticle[0]).toLowerCase().replace(" ", "-"));
                            hashMapArticle.put("title", arrayArticle[0]);
                            hashMapArticle.put("text", arrayArticle[1]);
                            hashMapArticle.put("description", arrayArticle[2]);
                            hashMapArticle.put("tags", arrayArticle[3]);
                            hashMapArticle.put("image", arrayArticle[4]);
                            hashMapArticle.put("date", arrayArticle[5]);
                            hashMapArticle.put("target", arrayArticle[6]);
                            if (articlesSkipEmptyRow) {
                                if (!hashMapArticle.get("name").isEmpty() && !hashMapArticle.get("title").isEmpty() && !hashMapArticle.get("tags").isEmpty() && !hashMapArticle.get("image").isEmpty() && !hashMapArticle.get("date").isEmpty()) {
                                    listArticles.add(hashMapArticle);
                                }
                            } else {
                                listArticles.add(hashMapArticle);
                            }
                        }
                    }
                }catch(Exception e) {
                    LOG.info("\nRemyJobScheduledTask: ERROR while importing Related Articles CSV - Import TXT "+ i +" {} ", e.toString());
                    isError = true;
                }
            }
            i++;
        }
        return listArticles;
    }

}