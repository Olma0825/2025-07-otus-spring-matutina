package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование репозитория для работы с Авторами")
@DataJpaTest
@Import(AuthorRepositoryImpl.class)
public class AuthorRepositoryImplTest {

    @Autowired
    private AuthorRepositoryImpl authorRepository;

    @Autowired
    private TestEntityManager em;

    private List<Author> authorList;

    private List<Author> getAuthorList() {
        List<Author> authors = IntStream.range(0, 3).boxed()
                .map(a -> {
                    Author author = new Author("Author_" + a);
                    em.persist(author);
                    return author;
                })
                .toList();
        em.flush();
        return authors;
    }

    @BeforeEach
    void setUp() {
        authorList = getAuthorList();
    }

    @Test
    @DisplayName("Должен загружать всех авторов")
    void shouldReturnAllAuthors() {
        var expectedAuthors = authorList;
        var actualAuthors = authorRepository.findAll();

        assertThat(actualAuthors)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(expectedAuthors);
    }

    @Test
    @DisplayName("Должен возвращать автора по id")
    void shouldReturnAuthorById() {
        var expectedAuthor = authorList.get(0);
        var actualAuthor = authorRepository.findById(expectedAuthor.getId());

        assertThat(actualAuthor).get().isEqualTo(expectedAuthor);
        assertThat(actualAuthor).get().extracting(Author::getFullName).isEqualTo(expectedAuthor.getFullName());
    }

    @Test
    @DisplayName("Должен возвращать пустой список когда авторов нет")
    void shouldReturnEmptyList_WhenNoAuthors() {
        em.clear();
        String jpql = "delete from Author";
        em.getEntityManager().createQuery(jpql).executeUpdate();

        List<Author> actualAuthors = authorRepository.findAll();

        assertThat(actualAuthors).isEmpty();
    }

    @Test
    @DisplayName("Должен возвращать Optional.empty() когда не находим автора с данным Id")
    void shouldReturnEmptyOptional_WhenAuthorDoesNotExist() {
        Optional<Author> actualAuthor = authorRepository.findById(1000L);

        assertThat(actualAuthor).isEmpty();
    }
}
