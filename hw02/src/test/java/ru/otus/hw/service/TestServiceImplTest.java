package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @InjectMocks
    private StudentServiceImpl studentService;

    private List<Question> testQuestions;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student("Ivan", "Ivanov");

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

    //это должно быть в StudentServiceImplTest.java
    @Test
    @DisplayName("Должен создавать студента")
    void shouldCreateStudent() {
        when(ioService.readStringWithPrompt("Please input your first name")).thenReturn("Ivan");
        when(ioService.readStringWithPrompt("Please input your last name")).thenReturn("Ivanov");

        Student student = studentService.determineCurrentStudent();

        assertThat(student.firstName()).isEqualTo("Ivan");
        assertThat(student.lastName()).isEqualTo("Ivanov");

    }

    @Test
    @DisplayName("Должен корректно выводить все вопросы с ответами")
    void shouldDisplayAllQuestionsWithAnswers() {
        when(questionDao.findAll()).thenReturn(testQuestions);
        when(ioService.readIntForRangeWithPrompt(anyInt(),anyInt(), anyString(), anyString())).thenReturn(2);

        TestResult result = testService.executeTestFor(student);
        List<Question> questions = result.getAnsweredQuestions();

        assertEquals("What is 2+2?", questions.get(0).text());
        assertEquals("Capital of Russia?", questions.get(1).text());

        String answer = String.join("%n", result.getAnsweredQuestions().get(0).answers().stream().map(Answer::text).toList());
        assertEquals("3%n4%n5", answer);

        answer = String.join("%n", result.getAnsweredQuestions().get(1).answers().stream().map(Answer::text).toList());
        assertEquals("London%nMoscow%nBerlin", answer);
    }

    @Test
    @DisplayName("Должен обрабатывать все вопросы")
    void shouldProcessAllQuestions() {
        when(questionDao.findAll()).thenReturn(testQuestions);
        when(ioService.readIntForRangeWithPrompt(anyInt(),anyInt(), anyString(), anyString())).thenReturn(2);

        TestResult result = testService.executeTestFor(student);

        assertThat(result.getAnsweredQuestions()).hasSize(2);
        assertThat(result.getRightAnswersCount()).isEqualTo(2);

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");
        verify(ioService, times(2)).readIntForRangeWithPrompt(anyInt(),anyInt(), anyString(), anyString());

    }

    @Test
    @DisplayName("Должен корректно обрабатывать пустой список вопросов")
    void shouldHandleEmptyQuestionsList() {
        when(questionDao.findAll()).thenReturn(List.of());

        TestResult result = testService.executeTestFor(student);

        assertThat(result).isNotNull();
        assertThat(result.getRightAnswersCount()).isZero();
        assertThat(result.getAnsweredQuestions()).isEmpty();

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");
        verify(ioService, times(0)).readIntForRangeWithPrompt(anyInt(),anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("должен корректно определять неправильные ответы")
    void shouldCorrectlyDetermineWrongAnswers() {
        Question singleQuestion = testQuestions.get(0);
        when(questionDao.findAll()).thenReturn(List.of(singleQuestion));
        when(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(1); // неправильный ответ

        TestResult result = testService.executeTestFor(student);

        assertThat(result.getRightAnswersCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("должен корректно определять правильные ответы")
    void shouldCorrectlyDetermineRightAnswers() {
        Question singleQuestion = testQuestions.get(0);
        when(questionDao.findAll()).thenReturn(List.of(singleQuestion));
        when(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(2);

        TestResult result = testService.executeTestFor(student);

        assertThat(result.getRightAnswersCount()).isEqualTo(1);
    }

}
