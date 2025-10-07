package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();

        return getTestResult(questions, student);
    }

    private TestResult getTestResult(List<Question> questions, Student student) {
        int minAnswerOption = 1;
        var testResult = new TestResult(student);
        for (var question: questions) {
            int maxAnswerOption = question.answers().size();
            var isAnswerValid = false;
            String errorMessage = "Error, enter a number in the range from %s to %s";
            int answer = ioService.readIntForRangeWithPrompt(
                    minAnswerOption, maxAnswerOption,
                    convertQuestionToString(question),
                    errorMessage);
            if (question.answers().get(answer - 1).isCorrect()) {
                isAnswerValid = true;
            }
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private String convertQuestionToString(Question question) {
        StringBuilder questionString = new StringBuilder(question.text());
        int counter = 0;
        for (Answer answer: question.answers()) {
            questionString.append(System.lineSeparator()).append(++counter).append(" ").append(answer.text());
        }
        return questionString.toString();
    }

}
