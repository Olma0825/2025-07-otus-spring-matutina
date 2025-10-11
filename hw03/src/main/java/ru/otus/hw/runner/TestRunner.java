package ru.otus.hw.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.TestRunnerService;

@Component
public class TestRunner implements ApplicationRunner {
    private final TestRunnerService testRunnerService;

    public TestRunner(TestRunnerService testRunnerService) {
        this.testRunnerService = testRunnerService;
    }

    @Override
    public void run(ApplicationArguments args) {
        testRunnerService.run();
    }
}
