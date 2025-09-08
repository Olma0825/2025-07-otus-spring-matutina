package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = getQuestions();
        questions.forEach(question -> {
            ioService.printLine(question.text());
            AtomicInteger counter = new AtomicInteger(1);
            question.answers()
                    .forEach(answer -> ioService.printLine(counter.getAndIncrement() + " " + answer.text()));
            int inputNumber = ioService.inputNumber();
        });
    }

    public List<Question> getQuestions() {
        return questionDao.findAll();
    }
}
