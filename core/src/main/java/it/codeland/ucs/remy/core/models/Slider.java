package it.codeland.ucs.remy.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;


@Model(adaptables = Resource.class, resourceType = { "academy-ucs-remy/components/structure/heroslider" })
@Exporter(name = "jackson", extensions = "json")

public class Slider {

    private static final Logger LOG = LoggerFactory.getLogger(Slider.class);

    @ValueMapValue(name=PROPERTY_RESOURCE_TYPE, injectionStrategy=InjectionStrategy.OPTIONAL)
    @Default(values="No resourceType")
    protected String resourceType;

    @SlingObject
    private Resource resource;
    @SlingObject
    private ResourceResolver resourceResolver;


    @PostConstruct
    protected void init() {
        
    }

    public List<Map<String, String>> getListMapTagSlider1() {
        return getListMapTagSlider("slider1Tag");
    }
    
    public List<Map<String, String>> getListMapTagSlider2() {
        return getListMapTagSlider("slider2Tag");
    }
    
    public List<Map<String, String>> getListMapTagSlider3() {
        return getListMapTagSlider("slider3Tag");
    }
    
    public List<Map<String, String>> getListMapTagSlider4() {
        return getListMapTagSlider("slider4Tag");
    }

    public List<Map<String, String>> getListMapTagSlider5() {
        return getListMapTagSlider("slider5Tag");
    }


    public List<Map<String, String>> getListMapTagSlider(String name) {
        ArrayList<Map<String, String>> listMapSlider = new ArrayList<>();
        try {
            Resource resourceField = resource.getChild(name+"s");
            if(resourceField != null) {
                for(Resource navItem : resourceField.getChildren()) {
                    Map<String,String> mapNavItem = new HashMap<>();
                    String tag = ""+navItem.getValueMap().get(name, String.class);
                    if(tag.contains("/")) {
                        tag = tag.substring(tag.lastIndexOf("/") + 1);
                    }
                    if(tag.endsWith(":")) {
                        tag = tag.replace(":", "");
                    }
                    if(tag.contains(":")) {
                        tag = tag.substring(tag.lastIndexOf(":") + 1);
                    }
                    mapNavItem.put(name, tag);
                    listMapSlider.add(mapNavItem);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Slider Tag {} Items {} ", name, e.toString());
        }
        return listMapSlider;
    }

}