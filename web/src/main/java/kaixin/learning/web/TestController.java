package kaixin.learning.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/echo/{input}")
    public String echo(@PathVariable String input) {
        return input;
    }

    @GetMapping("/compute/{number}")
    public Long compute(@PathVariable long number) {
        return LongStream.rangeClosed(1, number)
                         .reduce(1, (a, b) -> a * b);
    }

    private final AtomicLong requestCounter = new AtomicLong();

    @GetMapping("/delay/{ms}")
    public String delay(@PathVariable long ms) {
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

        try {
            Thread.sleep(ms); // Simulate blocking I/O
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted";
        }
        return "Delayed for " + ms + "ms";
    }
}
