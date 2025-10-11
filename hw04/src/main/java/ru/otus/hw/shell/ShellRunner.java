package ru.otus.hw.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
public class ShellRunner {
    private final TestRunnerService testRunnerService;

    public ShellRunner(TestRunnerService testRunnerService) {
        this.testRunnerService = testRunnerService;
    }

    @ShellMethod(key = {"r", "run", "start"}, value = "")
    public void start(@ShellOption(defaultValue = "en-US",
            value = {"-l", "-lang"},
            help = "Language code: en-US, ru-RU")
                          String lang) {

        testRunnerService.run(lang);
    }
}
