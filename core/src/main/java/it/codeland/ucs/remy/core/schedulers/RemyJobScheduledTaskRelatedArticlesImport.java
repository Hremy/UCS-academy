package it.codeland.ucs.remy.core.schedulers;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;


@Designate(ocd= RemyJobScheduledTaskRelatedArticlesImport.SlingSchedulerOSGIConfiguration.class)
@Component(service = Runnable.class,
        property = {
                "expression=*/5 * * * * ?"
        })

public class RemyJobScheduledTaskRelatedArticlesImport implements Runnable {

    @ObjectClassDefinition(
            name="RemyJobScheduledTaskRelatedArticlesImport",
            description = "Remy Job Scheduled Cron-Job Task to Import RelatedArticles"
    )
    public static @interface SlingSchedulerOSGIConfiguration  {

        @AttributeDefinition(
                name = "RemyJobScheduledTaskRelatedArticlesImport",
                description = "Remy Job Scheduled Cron-Job Task to Import RelatedArticles",
                type = AttributeType.STRING)
        String croneJobSchedulerName() default "RemyJobScheduledTaskRelatedArticlesImport";

        @AttributeDefinition(
                name = "Cron-job expression",
                description = "Cron expression used by the scheduler",
                type = AttributeType.STRING
        )
        String croneJobSchedulerExpression() default "0 * * * * ?";

        @AttributeDefinition(
                name = "Concurrent task",
                description = "Whether or not to schedule this task concurrently",
                type = AttributeType.BOOLEAN
        )
        boolean schedulerConcurrent() default true;

        @AttributeDefinition(
                name = "Enabled",
                description = "True, if Job-Crone Task Scheduler service is enabled",
                type = AttributeType.BOOLEAN)
        public boolean enabled() default false;

        @AttributeDefinition(
                name = "csvRelatedArticlesImportFile",
                description = "csv Related Articles Import File",
                type = AttributeType.STRING
        )
        String csvRelatedArticlesImportFile() default "/content/dam/academy-ucs-remy/csv-files/articles.csv";
    }

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @SlingObject
    private Resource resource;
    @SlingObject
    private ResourceResolver resourceResolver;
    @Reference
    private Scheduler scheduler;

    private int schedulerId;
    private String schedulerName;

    String csvRelatedArticlesImportFile;

    @Override
    public void run() {

        LOG.info("\n RemyJobScheduledTaskRelatedArticlesImport: is now running");

        try {

            Page pageHome = resourceResolver.adaptTo(PageManager.class).getPage("/content/ucs-exercise-remy/us/en");
            Resource resourcePage = resourceResolver.getResource(pageHome.getPath());
            Node nodePage = resourcePage.adaptTo(Node.class);
            Node nodePageJCRContent = nodePage.getNode("jcr:content");
            Node NodeContainer = nodePageJCRContent.getNode("parsys");
            Node nodeRelatedHashTags = NodeContainer.getNode("relatedhashtags");

            csvRelatedArticlesImportFile = nodeRelatedHashTags.hasProperty("csvFileArticleRef") ? String.valueOf(nodeRelatedHashTags.getProperty("csvFileArticleRef").getValue()) : csvRelatedArticlesImportFile;

        } catch (Exception e) {
            LOG.info("\nRemyJobScheduledTaskRelatedArticlesImport: ERROR while importing Related Articles CSV {} ", e.toString());
        }

        LOG.info("\nRemyJobScheduledTaskRelatedArticlesImport: is now running, csvRelatedArticlesImportFile='{}'", csvRelatedArticlesImportFile);

    }

    @Activate
    protected void activate(final SlingSchedulerOSGIConfiguration slingSchedulerOSGIConfiguration) {

        schedulerName = slingSchedulerOSGIConfiguration.croneJobSchedulerName();
        schedulerId = schedulerName.hashCode();

        csvRelatedArticlesImportFile = slingSchedulerOSGIConfiguration.csvRelatedArticlesImportFile();
    }

    @Modified
    protected void modified(SlingSchedulerOSGIConfiguration config) {

        removeScheduler();

        schedulerName = config.croneJobSchedulerName();
        schedulerId = schedulerName.hashCode();

        addScheduler(config);
    }

    @Deactivate
    protected void deactivate(SlingSchedulerOSGIConfiguration config) {
        removeScheduler();
    }

    private void removeScheduler() {

        LOG.info("\n RemyJobScheduledTaskRelatedArticlesImport: Removing scheduler: {}", schedulerId);

        scheduler.unschedule(String.valueOf(schedulerId));
    }

    private void addScheduler(SlingSchedulerOSGIConfiguration config) {

        if(config.enabled()) {

            ScheduleOptions scheduleOptions = scheduler.EXPR(config.croneJobSchedulerExpression());

            scheduleOptions.name(config.croneJobSchedulerName());
            scheduleOptions.canRunConcurrently(false);

            scheduler.schedule(this, scheduleOptions);

            LOG.info("\nRemyJobScheduledTaskRelatedArticlesImport: Scheduler added");

        } else {

            LOG.info("\nRemyJobScheduledTaskRelatedArticlesImport: Scheduler is disabled");

        }

    }

}