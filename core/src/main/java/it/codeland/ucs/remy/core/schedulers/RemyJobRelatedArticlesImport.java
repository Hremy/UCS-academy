package it.codeland.ucs.remy.core.schedulers;

import org.apache.commons.codec.binary.Base64;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@Designate(ocd= RemyJobRelatedArticlesImport.SlingSchedulerOSGIConfiguration.class)
@Component(service = Runnable.class,
        property = {
                "expression=*/5 * * * * ?"
        })
public class RemyJobRelatedArticlesImport implements Runnable {

    @ObjectClassDefinition(
            name="RemyJobRelatedArticlesImport",
            description = "Remy Job Scheduled Task for cron-job like task with properties"
    )
    public static @interface SlingSchedulerOSGIConfiguration {

        @AttributeDefinition(
                name = "Cron-job expression",
                description = "Cron expression used by the scheduler",
                type = AttributeType.STRING
        )
        String scheduler_expression() default "0 * * * * ?";

        @AttributeDefinition(
                name = "Concurrent task",
                description = "Whether or not to schedule this task concurrently",
                type = AttributeType.BOOLEAN
        )
        boolean scheduler_concurrent() default false;

        @AttributeDefinition(
                name = "Enabled",
                description = "True, if Job-Crone Task Scheduler service is enabled",
                type = AttributeType.BOOLEAN)
        public boolean enabled() default true;

        @AttributeDefinition(
                name = "A parameter",
                description = "Can be configured in /system/console/configMgr",
                type = AttributeType.STRING
        )
        String myParameter() default "Parameter Value";
    }

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @SlingObject
    private Resource resource;
    @SlingObject
    private ResourceResolver resourceResolver;

    @Reference
    private ResourceResolverFactory resolverFactory;

    private String myParameter;


    @Override
    public void run() {

        LOG.info("\nRemy-JobScheduledTask: STARTED : Importing Articles \n\n");

        try {

            URL url = new URL("http://localhost:4502/bin/remy-ucs-related-articles-import");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(("admin" + ":" + "admin").getBytes(StandardCharsets.UTF_8))));
            httpURLConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder responseContent = new StringBuilder();
            String content;
            while ((content = br.readLine()) != null) {
                responseContent.append(content);
            }

            String result = responseContent.toString().replace("<br>", "\n");

            LOG.info("\nRemy-JobScheduledTask: SUCCESS : Importing Articles " +" --> "+ result +"\n\n");

        } catch (Exception e) {
            LOG.info("\nRemy-JobScheduledTask: FAILED : Importing Articles "+ e +"\n\n");
        }

        LOG.info("\nRemy-JobScheduledTask: STOPPED : Importing Articles \n\n");

    }

    @Activate
    protected void activate(final SlingSchedulerOSGIConfiguration slingSchedulerOSGIConfiguration) {
        myParameter = slingSchedulerOSGIConfiguration.myParameter();
    }

}