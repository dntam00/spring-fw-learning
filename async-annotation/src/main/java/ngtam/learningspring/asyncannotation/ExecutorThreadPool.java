package ngtam.learningspring.asyncannotation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ExecutorThreadPool {

    private final ThreadPoolProperties properties;

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() throws InterruptedException {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(properties.getCoreSize());
        taskExecutor.setMaxPoolSize(properties.getMaxSize());
        taskExecutor.setQueueCapacity(properties.getQueueSize());
        taskExecutor.setThreadNamePrefix(properties.getPrefixName());
        taskExecutor.setPrestartAllCoreThreads(properties.isPreStart());
        taskExecutor.setRejectedExecutionHandler(properties.getPolicy().getHandler());
        taskExecutor.setBeanName("asyncExecutor");
        taskExecutor.initialize();
        log.info("Initialize ThreadPoolTaskExecutor: {}", properties);
        printStats(taskExecutor);

        return taskExecutor;
    }

    public void printStats(ThreadPoolTaskExecutor taskExecutor) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            ThreadPoolExecutor threadPool = taskExecutor.getThreadPoolExecutor();
            log.info("=========================");
            log.info("Pool Size: {}", threadPool.getPoolSize());
            log.info("Active Threads: {}", threadPool.getActiveCount());
            log.info("Number of Tasks Completed: {}", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
            log.info("=========================");

        }, 0, 5, TimeUnit.SECONDS);
    }

}
