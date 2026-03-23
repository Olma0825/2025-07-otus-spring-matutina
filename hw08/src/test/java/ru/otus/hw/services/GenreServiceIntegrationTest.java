package ru.otus.hw.services;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import({
        GenreServiceImpl.class,
})
@DisplayName("Интеграционные тесты GenreService")
public class GenreServiceIntegrationTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private MongoTemplate mongoTemplate;

    String genre1Id;
    String genre2Id;
    String genre3Id;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("comments");

        Genre genre1 = mongoTemplate.insert(new Genre("классическая проза"), "genres");
        Genre genre2 = mongoTemplate.insert(new Genre("историческая проза"), "genres");
        Genre genre3 = mongoTemplate.insert(new Genre("поэма"), "genres");
        genre1Id = genre1.getId();
        genre2Id = genre2.getId();
        genre3Id = genre3.getId();

        Author author = mongoTemplate.insert(new Author("Лев Толстой"), "authors");

        Book book1 = mongoTemplate.insert(new Book("Война и мир", author, genre1), "books");
        Book book2 = mongoTemplate.insert(new Book("Анна Каренина", author, genre1), "books");

    }

    @Test
    @DisplayName("Найти жанр по айди")
    public void shouldReturnGenreById() {
        GenreDto actualGenre = genreService.findById(genre1Id);

        assertThat(actualGenre.name()).isEqualTo("классическая проза");
    }

    @Test
    @DisplayName("Найти все жанры")
    public void shouldReturnAllGenre() {
        List<GenreDto> actualGenre = genreService.findAll();

        assertThat(actualGenre)
                .isNotNull()
                .hasSize(3);

        assertThat(actualGenre)
                .extracting(GenreDto::name)
                .containsExactlyInAnyOrder("классическая проза", "историческая проза", "поэма");
    }

    @Test
    @DisplayName("Должен создавать новый жанр")
    void shouldInsertGenre() {
        List<GenreDto> beforeGenres = genreService.findAll();
        assertThat(beforeGenres).hasSize(3);

        GenreDto newGenre = genreService.insert("Новый жанр");

        assertThat(newGenre).isNotNull();
        assertThat(newGenre.id()).isNotBlank();
        assertThat(newGenre.name()).isEqualTo("Новый жанр");

        List<GenreDto> afterGenres = genreService.findAll();

        assertThat(afterGenres).hasSize(4);
        assertThat(afterGenres).extracting(GenreDto::name)
                .containsExactlyInAnyOrder("Новый жанр", "классическая проза", "историческая проза", "поэма");
    }

    @Test
    @DisplayName("Должен обновлять жанр")
    void shouldUpdateGenre() {
        String updatedName = "обновленная классика";

        GenreDto updatedGenre = genreService.update(genre1Id, updatedName);

        assertThat(updatedGenre).isNotNull();
        assertThat(updatedGenre.id()).isEqualTo(genre1Id);
        assertThat(updatedGenre.name()).isEqualTo(updatedName);

        GenreDto foundGenre = genreService.findById(genre1Id);
        assertThat(foundGenre.name()).isEqualTo(updatedName);
    }

    @Test
    @DisplayName("Должен удалять жанр без книг")
    void shouldDeleteGenre() {
        long booksCount = mongoTemplate.count(
                new Query(Criteria.where("genre.$id").is(genre3Id)),
                Book.class
        );
        assertThat(booksCount).isZero();

        GenreDto foundGenre = genreService.findById(genre3Id);
        assertThat(foundGenre).isNotNull();
        assertThat(foundGenre.name()).isEqualTo("поэма");

        genreService.delete(genre3Id);

        assertThatThrownBy(() -> genreService.findById(genre3Id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Genre with id=" + genre3Id + " not found");

    }

    @Test
    @DisplayName("Должен вызывать исключение при удалении жанра, у которого есть книги")
    void shouldRaiseExceptionByDeleteGenre() {
        ObjectId objectId = new ObjectId(genre1Id);
        long booksCount = mongoTemplate.count(
                new Query(Criteria.where("genre.$id").is(objectId)),
                Book.class
        );
        assertThat(booksCount).isGreaterThan(0);
        assertThat(booksCount).isEqualTo(2);

        GenreDto foundGenre = genreService.findById(genre1Id);

        assertThat(foundGenre).isNotNull();
        assertThat(foundGenre.name()).isEqualTo("классическая проза");

        assertThatThrownBy(() -> genreService.delete(genre1Id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("You can't delete a genre from id = " + genre1Id
                        + " because there are books of this genre.");

        assertThat(genreService.findById(genre1Id)).isNotNull();
    }

}
