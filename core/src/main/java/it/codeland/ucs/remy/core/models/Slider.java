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
        ArrayList<Map<String, String>> listMapSlider = new ArrayList<>();
        try {
            Resource resourceField = resource.getChild("slider1Tags");
            if(resourceField != null) {
                for(Resource navItem : resourceField.getChildren()) {
                    Map<String,String> mapNavItem = new HashMap<>();
                    String tag = ""+navItem.getValueMap().get("slider1Tag", String.class);
                    if(tag.contains("/")) {
                        tag = tag.substring(tag.lastIndexOf("/") + 1);
                    }
                    if(tag.endsWith(":")) {
                        tag = tag.replace(":", "");
                    }
                    if(tag.contains(":")) {
                        tag = tag.substring(tag.lastIndexOf(":") + 1);
                    }
                    mapNavItem.put("slider1Tag", tag);
                    listMapSlider.add(mapNavItem);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Slider Tag 1 Items {} ", e.toString());
        }
        return listMapSlider;
    }


    public List<Map<String, String>> getListMapTagSlider2() {
        ArrayList<Map<String, String>> listMapSlider = new ArrayList<>();
        try {
            Resource resourceField = resource.getChild("slider2Tags");
            if(resourceField != null) {
                for(Resource navItem : resourceField.getChildren()) {
                    Map<String,String> mapNavItem = new HashMap<>();
                    String tag = ""+navItem.getValueMap().get("slider2Tag", String.class);
                    if(tag.contains("/")) {
                        tag = tag.substring(tag.lastIndexOf("/") + 1);
                    }
                    if(tag.endsWith(":")) {
                        tag = tag.replace(":", "");
                    }
                    if(tag.contains(":")) {
                        tag = tag.substring(tag.lastIndexOf(":") + 1);
                    }
                    mapNavItem.put("slider2Tag", tag);
                    listMapSlider.add(mapNavItem);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Slider Tag 2 Items {} ", e.toString());
        }
        return listMapSlider;
    }


    public List<Map<String, String>> getListMapTagSlider3() {
        ArrayList<Map<String, String>> listMapSlider = new ArrayList<>();
        try {
            Resource resourceField = resource.getChild("slider3Tags");
            if(resourceField != null) {
                for(Resource navItem : resourceField.getChildren()) {
                    Map<String,String> mapNavItem = new HashMap<>();
                    String tag = ""+navItem.getValueMap().get("slider3Tag", String.class);
                    if(tag.contains("/")) {
                        tag = tag.substring(tag.lastIndexOf("/") + 1);
                    }
                    if(tag.endsWith(":")) {
                        tag = tag.replace(":", "");
                    }
                    if(tag.contains(":")) {
                        tag = tag.substring(tag.lastIndexOf(":") + 1);
                    }
                    mapNavItem.put("slider3Tag", tag);
                    listMapSlider.add(mapNavItem);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Slider Tag 3 Items {} ", e.toString());
        }
        return listMapSlider;
    }


    public List<Map<String, String>> getListMapTagSlider4() {
        ArrayList<Map<String, String>> listMapSlider = new ArrayList<>();
        try {
            Resource resourceField = resource.getChild("slider4Tags");
            if(resourceField != null) {
                for(Resource navItem : resourceField.getChildren()) {
                    Map<String,String> mapNavItem = new HashMap<>();
                    String tag = ""+navItem.getValueMap().get("slider4Tag", String.class);
                    if(tag.contains("/")) {
                        tag = tag.substring(tag.lastIndexOf("/") + 1);
                    }
                    if(tag.endsWith(":")) {
                        tag = tag.replace(":", "");
                    }
                    if(tag.contains(":")) {
                        tag = tag.substring(tag.lastIndexOf(":") + 1);
                    }
                    mapNavItem.put("slider4Tag", tag);
                    listMapSlider.add(mapNavItem);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Slider Tag 4 Items {} ", e.toString());
        }
        return listMapSlider;
    }


    public List<Map<String, String>> getListMapTagSlider5() {
        ArrayList<Map<String, String>> listMapSlider = new ArrayList<>();
        try {
            Resource resourceField = resource.getChild("slider5Tags");
            if(resourceField != null) {
                for(Resource navItem : resourceField.getChildren()) {
                    Map<String,String> mapNavItem = new HashMap<>();
                    String tag = ""+navItem.getValueMap().get("slider5Tag", String.class);
                    if(tag.contains("/")) {
                        tag = tag.substring(tag.lastIndexOf("/") + 1);
                    }
                    if(tag.endsWith(":")) {
                        tag = tag.replace(":", "");
                    }
                    if(tag.contains(":")) {
                        tag = tag.substring(tag.lastIndexOf(":") + 1);
                    }
                    mapNavItem.put("slider5Tag", tag);
                    listMapSlider.add(mapNavItem);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Slider Tag 5 Items {} ", e.toString());
        }
        return listMapSlider;
    }

}