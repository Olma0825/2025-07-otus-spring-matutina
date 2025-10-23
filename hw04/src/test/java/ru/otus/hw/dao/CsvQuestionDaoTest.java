package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.shell.enabled=false",
        "spring.shell.interactive.enabled=false"
}, classes = CsvQuestionDao.class)
public class CsvQuestionDaoTest {

    @Autowired
    private CsvQuestionDao csvQuestionDao;

    @MockitoBean
    private TestFileNameProvider fileNameProvider;

    private List<Question> expectedQuestions;

    @BeforeEach
    void setUp() {
        expectedQuestions = List.of(
                new Question("What is 2+2?", List.of(
                        new Answer("3", false),
                        new Answer("4", true),
                        new Answer("5", false)
                )),
                new Question("Capital of Russia?", List.of(
                        new Answer("London", false),
                        new Answer("Moscow", true),
                        new Answer("Berlin", false)
                )));
    }

    @Test
    void shouldReadQuestionsFromCsvFile() {

        when(fileNameProvider.getTestFileName()).thenReturn("test-questions.csv");

        List<Question> actualQuestions = csvQuestionDao.findAll();

        assertThat(actualQuestions).isEqualTo(expectedQuestions);
    }
}
