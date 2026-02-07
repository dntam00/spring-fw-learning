package kaixin.learning.threadperrequest.controller;

import kaixin.learning.threadperrequest.model.RequestResult;
import kaixin.learning.threadperrequest.model.ThreadPoolSnapshot;
import kaixin.learning.threadperrequest.service.ThreadTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.LongStream;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DemoController {

    private final ThreadTracker threadTracker;

    /**
     * Returns immediately. Demonstrates that even fast requests acquire a thread.
     */
    @GetMapping("/fast")
    public RequestResult fast() {
        long requestId = threadTracker.nextRequestId();
        String threadName = Thread.currentThread().getName();
        Instant start = Instant.now();

        // No work — returns instantly
        Instant end = Instant.now();
        long duration = end.toEpochMilli() - start.toEpochMilli();

        ThreadPoolSnapshot snapshot = threadTracker.getThreadPoolSnapshot();
        threadTracker.logRequest(requestId, threadName, "/api/fast", duration);

        return RequestResult.builder()
                .thread(threadName)
                .endpoint("/api/fast")
                .startTime(start.toString())
                .endTime(end.toString())
                .durationMs(duration)
                .activeThreads(snapshot.getActiveThreads())
                .poolSize(snapshot.getCurrentThreadCount())
                .build();
    }

    /**
     * Simulates blocking I/O (e.g. database query, external HTTP call).
     * The Tomcat thread is held for the entire sleep duration.
     */
    @GetMapping("/slow/{seconds}")
    public RequestResult slow(@PathVariable int seconds) throws InterruptedException {
        long requestId = threadTracker.nextRequestId();
        String threadName = Thread.currentThread().getName();
        Instant start = Instant.now();

        // Simulate blocking I/O — thread is held idle for the full duration
        Thread.sleep(seconds * 1000L);

        Instant end = Instant.now();
        long duration = end.toEpochMilli() - start.toEpochMilli();

        ThreadPoolSnapshot snapshot = threadTracker.getThreadPoolSnapshot();
        threadTracker.logRequest(requestId, threadName, "/api/slow/" + seconds, duration);

        return RequestResult.builder()
                .thread(threadName)
                .endpoint("/api/slow/" + seconds)
                .startTime(start.toString())
                .endTime(end.toString())
                .durationMs(duration)
                .activeThreads(snapshot.getActiveThreads())
                .poolSize(snapshot.getCurrentThreadCount())
                .build();
    }

    /**
     * CPU-bound work. The thread is busy computing for the entire duration.
     */
    @GetMapping("/cpu/{iterations}")
    public RequestResult cpu(@PathVariable long iterations) {
        long requestId = threadTracker.nextRequestId();
        String threadName = Thread.currentThread().getName();
        Instant start = Instant.now();

        // CPU-intensive computation — thread is actively working
        long result = LongStream.rangeClosed(1, iterations)
                .reduce(0, (a, b) -> a + (long) Math.sqrt(b * Math.PI));

        Instant end = Instant.now();
        long duration = end.toEpochMilli() - start.toEpochMilli();

        ThreadPoolSnapshot snapshot = threadTracker.getThreadPoolSnapshot();
        threadTracker.logRequest(requestId, threadName, "/api/cpu/" + iterations, duration);

        return RequestResult.builder()
                .thread(threadName)
                .endpoint("/api/cpu/" + iterations)
                .startTime(start.toString())
                .endTime(end.toString())
                .durationMs(duration)
                .activeThreads(snapshot.getActiveThreads())
                .poolSize(snapshot.getCurrentThreadCount())
                .build();
    }
}
