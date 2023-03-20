package it.codeland.ucs.remy.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.codeland.ucs.remy.core.servlets.ServletRelatedArticlesImport;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.Optional;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;


@Model(adaptables = Resource.class, resourceType = { "academy-ucs-remy/components/structure/articlepage" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(selector = "export", name="jackson", extensions = "json", options = { @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })

public class ModelRelatedHashtags {

    private static final Logger LOG = LoggerFactory.getLogger(ModelRelatedHashtags.class);

    @ValueMapValue(name=PROPERTY_RESOURCE_TYPE, injectionStrategy=InjectionStrategy.OPTIONAL)
    @Default(values="No resourceType")
    protected String resourceType;

    @SlingObject
    private Resource resource;
    @SlingObject
    private ResourceResolver resourceResolver;


    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        String currentPagePath = Optional.ofNullable(pageManager)
                .map(pm -> pm.getContainingPage(resource))
                .map(Page::getPath).orElse("");
    }

    public List<Map<String, String>> getRelatedHashtags() {

        String out = "";
        // out = ServletRelatedArticlesImport.importArticles(resource, resourceResolver, LOG);

        ArrayList<Map<String, String>> listMapRelatedHashtag = new ArrayList<>();
        try {
            Resource resourceField = resource.getChild("linksMapRelatedHashtags");
            if(resourceField != null) {
                for(Resource itemHashtag : resourceField.getChildren()) {
                    Map<String,String> mapHashtag = new HashMap<>();
                    String hashtag = ""+itemHashtag.getValueMap().get("hashtag", String.class);
                    if(hashtag.contains("/")) {
                        hashtag = hashtag.substring(hashtag.lastIndexOf("/") + 1);
                    }
                    if(hashtag.endsWith(":")) {
                        hashtag = hashtag.replace(":", "");
                    }
                    if(hashtag.contains(":")) {
                        hashtag = hashtag.substring(hashtag.lastIndexOf(":") + 1);
                    }
                    mapHashtag.put("hashtag", hashtag);
                    listMapRelatedHashtag.add(mapHashtag);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Related Hashtag Items {} ", e.toString());
        }

        LOG.info("\n\nJobScheduledTask: SUCCESS : Importing Articles : From Page " +" - "+ out);

        return listMapRelatedHashtag;
    }

}

