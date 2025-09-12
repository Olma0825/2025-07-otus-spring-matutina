package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.AnswerReadException;
import ru.otus.hw.exceptions.QuestionReadException;

@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    @Override
    public void run() {
        try {
            testService.executeTest();
        } catch (QuestionReadException questionReadException) {
            System.out.println("Error when reading CSV file");
        } catch (AnswerReadException answerReadException) {
            System.out.println("Error. The answer must be an integer");
        } catch (Exception e) {
            System.out.println("Error ");
        }
    }
}
