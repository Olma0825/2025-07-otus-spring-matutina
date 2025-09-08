package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class TestServiceImplTest {

    @Mock
    private QuestionDao questionDao;

    private TestServiceImpl testService;

    private List<Question> testQuestions;

    private IOService ioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testService = new TestServiceImpl(ioService, questionDao);
        Question question1 = new Question("What is 2+2?",
                Arrays.asList(
                        new Answer("3", false),
                        new Answer("4", true),
                        new Answer("5", false)
                ));

        Question question2 = new Question("Capital of Russia?",
                Arrays.asList(
                        new Answer("London", false),
                        new Answer("Moscow", true),
                        new Answer("Berlin", false)
                ));

        testQuestions = Arrays.asList(question1, question2);
    }

    @Test
    void getQuestions_ShouldReturnQuestionsFromDao() {
        when(questionDao.findAll()).thenReturn(testQuestions);
        List<Question> result = testService.getQuestions();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is 2+2?", result.get(0).text());
        assertEquals("Capital of Russia?", result.get(1).text());
        verify(questionDao, times(1)).findAll();
    }
}
