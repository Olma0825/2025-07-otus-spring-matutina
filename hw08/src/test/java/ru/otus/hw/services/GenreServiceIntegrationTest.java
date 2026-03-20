package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.BookRepositoryCustomImpl;
import ru.otus.hw.repositories.CommentRepositoryCustomImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import({
        CommentServiceImpl.class,
        BookServiceImpl.class,
        GenreServiceImpl.class,
        AuthorServiceImpl.class,
        BookRepositoryCustomImpl.class,
        CommentRepositoryCustomImpl.class
})
@DisplayName("Интеграционные тесты GenreService")
public class GenreServiceIntegrationTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private GenreDto genre1;
    private GenreDto genre2;
    private BookDto book1;
    private BookDto book2;

    String genre1Id;
    String genre2Id;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("comments");

        genre1 = genreService.insert("классическая проза");
        genre2 = genreService.insert("историческая проза");

        AuthorDto author = authorService.insert("Лев Толстой");

        genre1Id = genre1.id();
        genre2Id = genre2.id();

        book1 = bookService.insert("Война и мир", author.id(), genre1.id());
        book2 = bookService.insert("Анна Каренина", author.id(), genre1.id());

        commentService.insert(book1.id(), "Комментарий 1");
        commentService.insert(book1.id(), "Комментарий 2");
        commentService.insert(book2.id(), "Комментарий 3");
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
                .hasSize(2);

        assertThat(actualGenre)
                .extracting(GenreDto::name)
                .containsExactlyInAnyOrder("классическая проза", "историческая проза");
    }

    @Test
    @DisplayName("Должен создавать новый жанр")
    void shouldInsertGenre() {
        List<GenreDto> beforeGenres = genreService.findAll();
        assertThat(beforeGenres).hasSize(2);

        GenreDto newGenre = genreService.insert("Новый жанр");

        assertThat(newGenre).isNotNull();
        assertThat(newGenre.id()).isNotBlank();
        assertThat(newGenre.name()).isEqualTo("Новый жанр");

        List<GenreDto> afterGenres = genreService.findAll();

        assertThat(afterGenres).hasSize(3);
        assertThat(afterGenres).extracting(GenreDto::name)
                .containsExactlyInAnyOrder("Новый жанр", "классическая проза", "историческая проза");
    }

    @Test
    @DisplayName("Должен обновлять жанр")
    void shouldUpdateGenre() {
        String updatedName = "обновленная классика";

        GenreDto updatedGenre = genreService.update(genre1.id(), updatedName);

        assertThat(updatedGenre).isNotNull();
        assertThat(updatedGenre.id()).isEqualTo(genre1.id());
        assertThat(updatedGenre.name()).isEqualTo(updatedName);

        GenreDto foundGenre = genreService.findById(genre1.id());
        assertThat(foundGenre.name()).isEqualTo(updatedName);
    }

    @Test
    @DisplayName("Должен удалять жанр и все связанные с ним книги и комментарии")
    void shouldDeleteGenre() {
        assertThat(bookService.findByGenreId(genre1.id())).hasSize(2);
        assertThat(commentService.findByBookId(book1.id())).hasSize(2);
        assertThat(commentService.findByBookId(book2.id())).hasSize(1);
        assertThat(genreService.findById(genre1.id())).isNotNull();

        genreService.delete(genre1.id());

        assertThatThrownBy(() -> genreService.findById(genre1.id()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Genre with id=" + genre1.id() + " not found");

        assertThatThrownBy(() -> bookService.findById(book1.id()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id=" + book1.id() + " not found");

        assertThatThrownBy(() -> bookService.findById(book2.id()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id=" + book2.id() + " not found");

        assertThat(commentService.findByBookId(book1.id())).isEmpty();
        assertThat(commentService.findByBookId(book2.id())).isEmpty();

        assertThat(genreService.findById(genre2.id())).isNotNull();
        assertThat(genreService.findAll()).hasSize(1);
    }
}
