package it.codeland.ucs.remy.core.models;

import com.day.cq.wcm.api.Page;
import it.codeland.ucs.remy.core.servlets.ServletRelatedArticles;
import it.codeland.ucs.remy.core.utils.Article;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.jcr.Node;

@Model(adaptables = Resource.class, resourceType = { "academy-ucs-remy/components/structure/articlepage" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(selector = "exporter", extensions = "json", name = "jackson", options = { @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class ModelArticleExport {

    @SlingObject
    private Resource resource;
    @SlingObject
    private ResourceResolver resourceResolver;

    Article article;

    ValueMap valueMapHashTag;

    @PostConstruct
    protected void init() {

        String path = resource.getPath();
        path = path.replace("/jcr:content", "");

        Node nodeArticle = resourceResolver.getResource(path).adaptTo(Node.class);

        article = ServletRelatedArticles.getArticle(resource.getResourceResolver(), nodeArticle);

        Page homePage = OtherPage.getHomePage(resourceResolver);

        valueMapHashTag = resourceResolver.getResource(homePage.getPath() + "/jcr:content/parsys/relatedhashtags").getValueMap();

    }

    public String getTitle() {
        String title = "";
        if(valueMapHashTag.get("title", false)) {
            title = article.getTitle();
        }
        return title;
    }

    public String getDescription() {
        String description = "";
        if(valueMapHashTag.get("description", false)) {
            description = article.getDescription();
        }
        return description;
    }

    public String getImage() {
        String image = "";
        if(valueMapHashTag.get("image", false)) {
            image = article.getImage();
        }
        return image;
    }

    public String getDate() {
        String date = "";
        if(valueMapHashTag.get("date", false)) {
            date = article.getDate();
        }
        return date;
    }

    public String getTags() {
        String tags = "";
        if(valueMapHashTag.get("tags", false)) {
            tags = article.getTags();
        }
        return tags;
    }

    public String getPath() {
        String path = "";
        if(valueMapHashTag.get("path", false)) {
            path = article.getPath();
        }
        return path;
    }

    public String getLink() {
        String link = "";
        if(valueMapHashTag.get("link", false)) {
            link = article.getLink();
        }
        return link;
    }

}