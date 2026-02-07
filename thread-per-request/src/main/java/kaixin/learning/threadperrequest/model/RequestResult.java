package kaixin.learning.threadperrequest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestResult {

    private String thread;
    private String endpoint;
    private String startTime;
    private String endTime;
    private long durationMs;
    private int activeThreads;
    private int poolSize;
}
