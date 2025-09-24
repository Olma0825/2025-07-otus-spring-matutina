package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

    @Mock
    TestFileNameProvider fileNameProvider;

    @Test
    void shouldReadQuestionsFromCsvFile() {

        when(fileNameProvider.getTestFileName()).thenReturn("test-questions.csv");
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        CsvQuestionDao dao = new CsvQuestionDao(fileNameProvider, resourceLoader);

        List<Question> questions = dao.findAll();

        assertThat(questions).hasSize(2);

        Question firstQuestion = questions.get(0);
        assertThat(firstQuestion.text()).isEqualTo("What is 2+2?");

        List<Answer> firstQuestionAnswers = firstQuestion.answers();
        assertThat(firstQuestionAnswers).hasSize(3);
        assertThat(firstQuestionAnswers.get(0).text()).isEqualTo("3");
        assertThat(firstQuestionAnswers.get(0).isCorrect()).isFalse();
        assertThat(firstQuestionAnswers.get(1).text()).isEqualTo("4");
        assertThat(firstQuestionAnswers.get(1).isCorrect()).isTrue();
        assertThat(firstQuestionAnswers.get(2).text()).isEqualTo("5");
        assertThat(firstQuestionAnswers.get(2).isCorrect()).isFalse();

        Question secondQuestion = questions.get(1);
        assertThat(secondQuestion.text()).isEqualTo("Capital of Russia?");

        List<Answer> secondQuestionAnswers = secondQuestion.answers();
        assertThat(secondQuestionAnswers).hasSize(3);
        assertThat(secondQuestionAnswers.get(0).text()).isEqualTo("London");
        assertThat(secondQuestionAnswers.get(0).isCorrect()).isFalse();
        assertThat(secondQuestionAnswers.get(1).text()).isEqualTo("Moscow");
        assertThat(secondQuestionAnswers.get(1).isCorrect()).isTrue();
        assertThat(secondQuestionAnswers.get(2).text()).isEqualTo("Berlin");
        assertThat(secondQuestionAnswers.get(2).isCorrect()).isFalse();
    }
}
