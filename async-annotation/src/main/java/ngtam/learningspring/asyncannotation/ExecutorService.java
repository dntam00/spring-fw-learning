package ngtam.learningspring.asyncannotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExecutorService {

    @Async("asyncExecutor")
    public void sample() {
        log.info("call execute");
    }
}
