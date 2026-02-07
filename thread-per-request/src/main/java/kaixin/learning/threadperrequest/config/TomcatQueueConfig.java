package kaixin.learning.threadperrequest.config;

import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class TomcatQueueConfig {

    @Value("${server.tomcat.task-queue-capacity:Integer.MAX_VALUE}")
    private int taskQueueCapacity;

    @Bean
    TomcatServletWebServerFactory tomcatFactory(ServerProperties serverProperties) {
        var tomcatProps = serverProperties.getTomcat();
        int maxThreads = tomcatProps.getThreads().getMax();
        int minSpare = tomcatProps.getThreads().getMinSpare();

        return new TomcatServletWebServerFactory() {
            @Override
            protected void customizeConnector(
                    org.apache.catalina.connector.Connector connector) {
                super.customizeConnector(connector);

                TaskQueue taskQueue = new TaskQueue(taskQueueCapacity);
                TaskThreadFactory threadFactory = new TaskThreadFactory(
                        "tomcat-exec-", true, Thread.NORM_PRIORITY);

                ThreadPoolExecutor executor = new ThreadPoolExecutor(
                        minSpare, maxThreads, 60, TimeUnit.SECONDS, taskQueue, threadFactory);
                taskQueue.setParent(executor);

                connector.getProtocolHandler().setExecutor(executor);
            }
        };
    }
}
