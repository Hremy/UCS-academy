package it.codeland.ucs.remy.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = {Resource.class})
public class OtherPage {
    @ValueMapValue(name = "sling:resourceType", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = {"No resourceType"})
    protected String resourceType;

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @PostConstruct
    protected void init() {}

    public Page getHomePage() {
        PageManager pageManager = (PageManager)this.resourceResolver.adaptTo(PageManager.class);
        Resource homeResource = this.resourceResolver.getResource("/content/academy-ucs-remy/us/en");
        Page homePage = pageManager.getContainingPage(homeResource);
        return homePage;
    }
}
