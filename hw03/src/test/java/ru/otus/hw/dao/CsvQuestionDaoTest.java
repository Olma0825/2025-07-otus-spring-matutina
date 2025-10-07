package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ResourceLoader;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

    @InjectMocks
    private CsvQuestionDao csvQuestionDao;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private TestFileNameProvider fileNameProvider;

    @Test
    void shouldReadQuestionsFromCsvFile() {

        when(fileNameProvider.getTestFileName()).thenReturn("test-questions.csv");

        List<Question> questions = csvQuestionDao.findAll();

        assertThat(questions).hasSize(2);

        Question firstQuestion = questions.get(0);
        assertThat(firstQuestion.text()).isEqualTo("What is 2+2?");

        List<Answer> firstQuestionAnswers = firstQuestion.answers();
        assertThat(firstQuestionAnswers).hasSize(3);
        assertThat(firstQuestionAnswers.stream().map(Answer::text).toList())
                .containsExactly("3", "4", "5");
        assertThat(firstQuestionAnswers.stream().map(Answer::isCorrect).toList())
                .containsExactly(false, true, false);

        Question secondQuestion = questions.get(1);
        assertThat(secondQuestion.text()).isEqualTo("Capital of Russia?");

        List<Answer> secondQuestionAnswers = secondQuestion.answers();
        assertThat(secondQuestionAnswers).hasSize(3);
        assertThat(secondQuestionAnswers.stream().map(Answer::text).toList())
                .containsExactly("London", "Moscow", "Berlin");
        assertThat(secondQuestionAnswers.stream().map(Answer::isCorrect).toList())
                .containsExactly(false, true, false);
    }
}
