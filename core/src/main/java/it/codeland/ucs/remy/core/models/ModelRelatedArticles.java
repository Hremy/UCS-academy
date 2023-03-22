package it.codeland.ucs.remy.core.models;

import it.codeland.ucs.remy.core.servlets.ServletRelatedArticles;
import it.codeland.ucs.remy.core.utils.Article;
import javax.annotation.PostConstruct;
import javax.jcr.Node;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {Resource.class}, resourceType = {"academy-ucs-remy/components/structure/articlepage"}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(selector = "export", name = "jackson", extensions = {"json"}, options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true")})
public class ModelRelatedArticles {
  private static final Logger LOG = LoggerFactory.getLogger(ModelRelatedArticles.class);
  
  @ValueMapValue(name = "sling:resourceType", injectionStrategy = InjectionStrategy.OPTIONAL)
  @Default(values = {"No resourceType"})
  protected String resourceType;
  
  @SlingObject
  private Resource resource;
  
  Article article;
  
  @PostConstruct
  protected void init() {
    Node nodeArticle = (Node)this.resource.adaptTo(Node.class);
    this.article = ServletRelatedArticles.getArticle(this.resource.getResourceResolver(), nodeArticle);
  }
  
  public String getTitle() {
    return this.article.getTitle();
  }
  
  public String getDescription() {
    return this.article.getDescription();
  }
  
  public String getHashtag() {
    return this.article.getHashtag();
  }
  
  public String getTags() {
    return this.article.getTags();
  }
  
  public String getImage() {
    return this.article.getImage();
  }
  
  public String getDate() {
    return this.article.getDate();
  }
  
  public String getPath() {
    return this.article.getPath();
  }
  
  public String getLink() {
    return this.article.getPath() + ".html";
  }
}
