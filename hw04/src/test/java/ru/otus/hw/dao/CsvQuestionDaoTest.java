package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
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
    private ResourceLoader resourceLoader;

    @MockitoBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void listAllBeans() {
        System.out.println("=== BEANS IN CONTEXT ===");
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            System.out.printf("%s (%s)%n", beanName, bean.getClass().getName());
        }
        System.out.println("Total beans: " + beanNames.length);
    }


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
