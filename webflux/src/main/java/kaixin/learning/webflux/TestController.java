package kaixin.learning.webflux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/echo/{input}")
    public Mono<String> echo(@PathVariable String input) {
        return Mono.just(input);
    }

    @GetMapping("/compute/{number}")
    public Mono<Long> compute(@PathVariable long number) {
        return Mono.fromCallable(() -> {
            // Simulate CPU-bound work
            return LongStream.rangeClosed(1, number)
                             .reduce(1, (a, b) -> a * b);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private final AtomicLong requestCounter = new AtomicLong();

    @GetMapping("/delay/{ms}")
    public Mono<String> delay(@PathVariable long ms) {
        long req = requestCounter.incrementAndGet();

        if (req % 10000 == 0) {
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            int liveThreads = threadBean.getThreadCount();
            int peakThreads = threadBean.getPeakThreadCount();

            System.out.println("=== Thread Info ===");
            System.out.println("Live threads: " + liveThreads);
            System.out.println("Peak threads: " + peakThreads);
            System.out.println("===================");
        }

        return Mono.delay(Duration.ofMillis(ms))
                   .thenReturn("Delayed for " + ms + "ms");
    }
}
