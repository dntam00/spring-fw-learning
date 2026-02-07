package kaixin.learning.threadperrequest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThreadPoolSnapshot {

    private int maxThreads;
    private int currentThreadCount;
    private int activeThreads;
    private int availableThreads;
    private int queuedRequests;
    private int queueCapacity;
    private String message;
}
