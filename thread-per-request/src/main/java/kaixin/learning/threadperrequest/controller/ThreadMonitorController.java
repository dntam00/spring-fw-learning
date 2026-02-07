package kaixin.learning.threadperrequest.controller;

import kaixin.learning.threadperrequest.model.RequestLog;
import kaixin.learning.threadperrequest.model.ThreadPoolSnapshot;
import kaixin.learning.threadperrequest.service.ThreadTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/threads")
@RequiredArgsConstructor
public class ThreadMonitorController {

    private final ThreadTracker threadTracker;

    /**
     * Real-time snapshot of the Tomcat thread pool.
     * Poll this while sending concurrent requests to /api/slow to observe exhaustion.
     */
    @GetMapping("/status")
    public ThreadPoolSnapshot status() {
        return threadTracker.getThreadPoolSnapshot();
    }

    /**
     * Returns the last N request logs showing which thread handled which request.
     * Demonstrates the 1:1 mapping between threads and requests.
     */
    @GetMapping("/log")
    public List<RequestLog> log() {
        return threadTracker.getRecentLogs();
    }
}
