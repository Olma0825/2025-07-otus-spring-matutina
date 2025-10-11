package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.exceptions.QuestionReadException;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final LocalizedIOService ioService;

    private final AppProperties appProperties;

    @Override
    public void run() {
        try {
            var student = studentService.determineCurrentStudent();
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (QuestionReadException questionReadException) {
            ioService.readStringWithPromptLocalized("TestRunnerService.error.reading_question");
        } catch (Exception e) {
            ioService.readStringWithPromptLocalized("TestRunnerService.error");
        }
    }

    @Override
    public void run(String lang) {
        appProperties.setLocale(lang);
        run();
    }
}
