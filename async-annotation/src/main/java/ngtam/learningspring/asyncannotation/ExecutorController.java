package ngtam.learningspring.asyncannotation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class ExecutorController {

    private final ExecutorService executorService;

    @GetMapping("")
    public void execute() {
        executorService.sample();
    }

}
