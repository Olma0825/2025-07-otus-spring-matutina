package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = questionDao.findAll();
        for (Question question: questions) {
            ioService.printLine(convertQuestionToString(question));
        }
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
