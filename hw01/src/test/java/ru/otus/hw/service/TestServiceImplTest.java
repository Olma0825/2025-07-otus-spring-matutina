package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class
TestServiceImplTest {

    @Mock
    private QuestionDao questionDao;

    @Mock
    private IOService ioService;

    @InjectMocks
    private TestServiceImpl testService;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    private List<Question> testQuestions;

    @BeforeEach
    void setUp() {

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
    @DisplayName("Должен корректно выводить все вопросы с ответами")
    void shouldDisplayAllQuestionsWithAnswers() {
        when(questionDao.findAll()).thenReturn(testQuestions);

        testService.executeTest();

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");
        verify(ioService, times(3)).printLine(stringCaptor.capture());

        List<String> capturedOutput = stringCaptor.getAllValues();

        assertEquals(String.format("What is 2+2?%n1 3%n2 4%n3 5"), capturedOutput.get(1));
        assertEquals(String.format("Capital of Russia?%n1 London%n2 Moscow%n3 Berlin"), capturedOutput.get(2));

    }

    @Test
    @DisplayName("Должен корректно обрабатывать пустой список вопросов")
    void shouldHandleEmptyQuestionsList() {
        when(questionDao.findAll()).thenReturn(List.of());

        testService.executeTest();

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");

    }

    @Test
    @DisplayName("Должен корректно обрабатывать вопрос без ответов")
    void shouldHandleQuestionWithoutAnswers() {
        Question questionWithoutAnswers = new Question("Question without answers?", List.of());
        when(questionDao.findAll()).thenReturn(List.of(questionWithoutAnswers));

        testService.executeTest();

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");

        verify(ioService, times(2)).printLine(stringCaptor.capture());
        assertEquals("Question without answers?", stringCaptor.getValue());

    }
}
