package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с жанрами ")
@DataJpaTest
@Import(JpaGenreRepository.class)
public class JpaGenreRepositoryTest {

    @Autowired
    private JpaGenreRepository repository;

    @Autowired
    private TestEntityManager em;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    List<Genre> getDbGenres() {
        List<Genre> genreList =  IntStream.range(0, 6).boxed()
                .map(id -> {
                    Genre genre = new Genre("Genre_" + id);
                    em.persist(genre);
                    return genre;
                })
                .toList();
        em.flush();
        return genreList;
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var expectedGenres = dbGenres;
        var actualGenres = repository.findAll();

        assertThat(actualGenres)
                .hasSize(6)
                .containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    @DisplayName("должен загружать жанр по id")
    @Test
    void shouldReturnCorrectGenreById() {
        Genre expectedGenre = dbGenres.get(1);
        Optional<Genre> actualGenre = repository.findById(expectedGenre.getId());

        assertThat(actualGenre).get().isEqualTo(expectedGenre);
        assertThat(actualGenre.get().getName()).isEqualTo(expectedGenre.getName());
    }

    @Test
    @DisplayName("Должен возвращать пустой список когда жанров нет")
    void shouldReturnEmptyList_WhenNoGenres() {
        em.clear();
        String jpql = "delete from Genre";
        em.getEntityManager().createQuery(jpql).executeUpdate();

        List<Genre> actualGenres = repository.findAll();

        assertThat(actualGenres).isEmpty();
    }

    @Test
    @DisplayName("Должен возвращать Optional.empty() когда не находим жанр с данным Id")
    void shouldReturnEmptyOptional_WhenGenreDoesNotExist() {
        Optional<Genre> actualGenre = repository.findById(1000L);

        assertThat(actualGenre).isEmpty();
    }

}
