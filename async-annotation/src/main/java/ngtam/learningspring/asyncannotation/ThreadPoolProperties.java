package ngtam.learningspring.asyncannotation;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@ConfigurationProperties(prefix = "app.async.pool")
@Component
public class ThreadPoolProperties {

    private static final int DEFAULT_THREAD_POOL_SIZE = Math.max(Runtime.getRuntime().availableProcessors() * 2 - 1, 2);
    private static final int DEFAULT_THREAD_POOL_MAX_SIZE = DEFAULT_THREAD_POOL_SIZE * 3;
    private static final int DEFAULT_THREAD_POOL_QUEUE_SIZE = 100000;

    int coreSize = DEFAULT_THREAD_POOL_SIZE;
    int maxSize = DEFAULT_THREAD_POOL_MAX_SIZE;
    int queueSize = DEFAULT_THREAD_POOL_QUEUE_SIZE;
    String prefixName = "async-test-";
    boolean preStart;
    Policy policy = Policy.ABORT;

    @RequiredArgsConstructor
    public enum Policy {
        ABORT(ThreadPoolExecutor.AbortPolicy::new),
        CALLER_RUNS(ThreadPoolExecutor.CallerRunsPolicy::new),
        DISCARD(ThreadPoolExecutor.DiscardPolicy::new),
        DISCARD_OLDEST(ThreadPoolExecutor.DiscardOldestPolicy::new),
        DISCARD_LOGGING(DiscardLoggingPolicy::new);

        private final Supplier<RejectedExecutionHandler> policyInstance;

        public RejectedExecutionHandler getHandler() {
            return policyInstance.get();
        }
    }

    @Slf4j
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class DiscardLoggingPolicy implements RejectedExecutionHandler {
        AtomicLong accumulatedCount = new AtomicLong();

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            int queueSize = executor.getQueue().size();

            log.error("[DISCARD_EXEC] queue: {}, max pool: {}, accumulated count: {}",
                      queueSize,
                      executor.getMaximumPoolSize(),
                      accumulatedCount.incrementAndGet());
        }
    }
}
