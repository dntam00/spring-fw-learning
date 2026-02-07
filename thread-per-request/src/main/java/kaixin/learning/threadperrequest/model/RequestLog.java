package kaixin.learning.threadperrequest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestLog {

    private long requestId;
    private String thread;
    private String endpoint;
    private long durationMs;
}
