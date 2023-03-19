package it.codeland.ucs.remy.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.http.util.TextUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Value;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Optional;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;


@Model(adaptables = Resource.class, resourceType = { "academy-ucs-remy/components/structure/articlepage" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(selector = "export", name="jackson", extensions = "json", options = { @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })

public class ModelRelatedArticles {

    private static final Logger LOG = LoggerFactory.getLogger(ModelRelatedArticles.class);

    @ValueMapValue(name=PROPERTY_RESOURCE_TYPE, injectionStrategy=InjectionStrategy.OPTIONAL)
    @Default(values="No resourceType")
    protected String resourceType;

    @SlingObject
    private Resource resource;
    @SlingObject
    private ResourceResolver resourceResolver;


    String currentPagePath;

    @Inject
    String title;

    @Inject
    String headline;

    @Inject
    String description;

    @Inject
    String link;

    @Inject
    String tags;

    @Inject
    String image;

    @Inject
    String date;


    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        currentPagePath = Optional.ofNullable(pageManager)
                .map(pm -> pm.getContainingPage(resource))
                .map(Page::getPath).orElse("");
    }

    public String getTitle() {
        if(geMapRelatedArticle().size() > 0) {
            title = geMapRelatedArticle().get(0).get("title");
        }
        return title;
    }

    public String getHeadline() {
        if(geMapRelatedArticle().size() > 0) {
            headline = geMapRelatedArticle().get(0).get("headline");
        }
        return headline;
    }

    public String getDescription() {
        if(geMapRelatedArticle().size() > 0) {
            description = geMapRelatedArticle().get(0).get("description");
        }
        return description;
    }

    public String getLink() {
        if(geMapRelatedArticle().size() > 0) {
            link = geMapRelatedArticle().get(0).get("link");
        }
        return link;
    }

    public String getTags() {
        if(geMapRelatedArticle().size() > 0) {
            tags = geMapRelatedArticle().get(0).get("tags");
        }
        return tags;
    }

    public String getImage() {
        if(geMapRelatedArticle().size() > 0) {
            image = geMapRelatedArticle().get(0).get("image");
        }
        return image;
    }

    public String getDate() {
        if(geMapRelatedArticle().size() > 0) {
            date = geMapRelatedArticle().get(0).get("date");
        }
        return date;
    }

    public List<Map<String, String>> geMapRelatedArticle() {
        Map<String, String> mapRelatedArticle = new HashMap<>();
        try {
            currentPagePath = currentPagePath.replace("/jcr:content.export.json", "");
            Page pageMagazineArticle = resourceResolver.adaptTo(PageManager.class).getPage(currentPagePath);
            Resource resourcePageMagazine = resourceResolver.getResource(currentPagePath);
            Node nodePageMagazine = resourcePageMagazine.adaptTo(Node.class);
            Node nodePageMagazineJCRContent = nodePageMagazine.getNode("jcr:content");
            Node itemRelatedArticle = nodePageMagazineJCRContent.hasNode("related-articles") ? nodePageMagazineJCRContent.getNode("related-articles") : null;

            mapRelatedArticle.put("title", nodePageMagazineJCRContent.hasProperty("jcr:title") ? String.valueOf(nodePageMagazineJCRContent.getProperty("jcr:title").getValue()) : "");
            mapRelatedArticle.put("headline", nodePageMagazineJCRContent.hasProperty("jcr:pageTitle") ? String.valueOf(nodePageMagazineJCRContent.getProperty("jcr:title").getValue()) : "");
            mapRelatedArticle.put("description", nodePageMagazineJCRContent.hasProperty("jcr:description") ? String.valueOf(nodePageMagazineJCRContent.getProperty("jcr:title").getValue()) : "");
            mapRelatedArticle.put("link", pageMagazineArticle.getPath()+".html");

            mapRelatedArticle.put("tags", nodePageMagazineJCRContent.hasProperty("cq:tags") ? Arrays.toString(nodePageMagazineJCRContent.getProperty("cq:tags").getValues()) : "");
            Value[] valueTags = nodePageMagazineJCRContent.hasProperty("cq:tags") ? nodePageMagazineJCRContent.getProperty("cq:tags").getValues() : new Value[] {};

            if(itemRelatedArticle != null) {
                mapRelatedArticle.put("image", itemRelatedArticle.hasProperty("imageArticleRef") ? String.valueOf(itemRelatedArticle.getProperty("imageArticleRef").getValue()) : "");
                mapRelatedArticle.put("date", itemRelatedArticle.hasProperty("date") ? String.valueOf(itemRelatedArticle.getProperty("date").getDate().getTimeInMillis()) : "");
            }

            String itemHashtag = ""+mapRelatedArticle.get("hashtag");
            if(itemHashtag.contains("/")) {
                itemHashtag = itemHashtag.substring(itemHashtag.lastIndexOf("/") + 1);
            }
            if(itemHashtag.endsWith(":")) {
                itemHashtag = itemHashtag.replace(":", "");
            }
            if(itemHashtag.contains(":")) {
                itemHashtag = itemHashtag.substring(itemHashtag.lastIndexOf(":") + 1);
            }
            mapRelatedArticle.put("hashtag", itemHashtag);

            ArrayList<String> tags = new ArrayList<String>();
            for (int i = 0; i < valueTags.length; i++) {
                Value value = valueTags[i];
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
            mapRelatedArticle.put("tags", Arrays.toString(tags.toArray()));

            String itemDate1 = ""+mapRelatedArticle.get("date");
            if(!TextUtils.isEmpty(itemDate1)) {
                mapRelatedArticle.put("dateMillis", itemDate1);
                itemDate1 = new SimpleDateFormat("EEEE d MMM yyyy").format(new Date(Long.parseLong(itemDate1)));
                mapRelatedArticle.put("date", itemDate1);
            }

        }catch (Exception e){
            LOG.info("\n ERROR while getting Related Article {} ", e);
        }
        ArrayList<Map<String, String>> listMapRelatedArticle = new ArrayList<>();
        listMapRelatedArticle.add(mapRelatedArticle);
        return listMapRelatedArticle;
    }

}