package kaixin.learning.threadperrequest.service;

import kaixin.learning.threadperrequest.model.RequestLog;
import kaixin.learning.threadperrequest.model.ThreadPoolSnapshot;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ThreadTracker {

    private static final int MAX_LOG_SIZE = 100;

    private final ServletWebServerApplicationContext applicationContext;
    private final AtomicLong requestIdCounter = new AtomicLong(0);
    private final Deque<RequestLog> requestLogs = new ConcurrentLinkedDeque<>();

    public ThreadTracker(ServletWebServerApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public long nextRequestId() {
        return requestIdCounter.incrementAndGet();
    }

    public void logRequest(long requestId, String thread, String endpoint, long durationMs) {
        requestLogs.addLast(RequestLog.builder()
                .requestId(requestId)
                .thread(thread)
                .endpoint(endpoint)
                .durationMs(durationMs)
                .build());

        while (requestLogs.size() > MAX_LOG_SIZE) {
            requestLogs.pollFirst();
        }
    }

    public List<RequestLog> getRecentLogs() {
        return List.copyOf(requestLogs);
    }

    public ThreadPoolSnapshot getThreadPoolSnapshot() {
        try {
            TomcatWebServer tomcat = (TomcatWebServer) applicationContext.getWebServer();
            Connector connector = tomcat.getTomcat().getConnector();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) connector.getProtocolHandler().getExecutor();

            int maxThreads = executor.getMaximumPoolSize();
            int currentPoolSize = executor.getPoolSize();
            int activeThreads = executor.getActiveCount();
            int queueSize = executor.getQueue().size();
            int queueCapacity = queueSize + executor.getQueue().remainingCapacity();

            int available = maxThreads - activeThreads;
            String message;
            if (available == 0 && executor.getQueue().remainingCapacity() == 0) {
                message = "Thread pool AND queue full! New requests will be REJECTED.";
            } else if (available == 0) {
                message = "Thread pool exhausted! New requests will be queued (" + queueSize + "/" + queueCapacity + ").";
            } else {
                message = available + " thread(s) available.";
            }

            return ThreadPoolSnapshot.builder()
                    .maxThreads(maxThreads)
                    .currentThreadCount(currentPoolSize)
                    .activeThreads(activeThreads)
                    .availableThreads(available)
                    .queuedRequests(queueSize)
                    .queueCapacity(queueCapacity)
                    .message(message)
                    .build();
        } catch (Exception e) {
            return ThreadPoolSnapshot.builder()
                    .maxThreads(-1)
                    .currentThreadCount(-1)
                    .activeThreads(-1)
                    .availableThreads(-1)
                    .queuedRequests(-1)
                    .queueCapacity(-1)
                    .message("Unable to read Tomcat thread pool: " + e.getMessage())
                    .build();
        }
    }

}
