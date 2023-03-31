package it.codeland.ucs.remy.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)
public class ModelRelatedHashtags {
  private static final Logger LOG = LoggerFactory.getLogger(ModelRelatedHashtags.class);
  
  @ValueMapValue(name = "sling:resourceType", injectionStrategy = InjectionStrategy.OPTIONAL)
  @Default(values = {"No resourceType"})
  protected String resourceType;
  
  @SlingObject
  private Resource resource;
  
  @SlingObject
  private ResourceResolver resourceResolver;
  
  @PostConstruct
  protected void init() {}
  
  public List<Map<String, String>> getRelatedHashtags() {

    ArrayList<Map<String, String>> listMapRelatedHashtag = new ArrayList<>();

    try {

      Resource resourceField = this.resource.getChild("linksMapRelatedHashtags");

      if (resourceField != null)

        for (Resource itemHashtag : resourceField.getChildren()) {

          Map<String, String> mapHashtag = new HashMap<>();
          String tag = itemHashtag.getValueMap().get("hashtag", String.class);

          if (tag != null) {
            tag = tag.replace(":", "/");
            tag = "/content/cq:tags/" + tag;
            Resource resourceTag = this.resourceResolver.getResource(tag);

            if (resourceTag != null) {
              String hashtag = resourceTag.getValueMap().get("jcr:title", "");
              mapHashtag.put("tag", tag);
              mapHashtag.put("hashtag", hashtag);
              listMapRelatedHashtag.add(mapHashtag);
            }

          }

        }

    } catch (Exception e) {
      LOG.error("\n ERROR while getting Related Hashtag Items {} ", e.toString());
    }

    return listMapRelatedHashtag;
  }

}
