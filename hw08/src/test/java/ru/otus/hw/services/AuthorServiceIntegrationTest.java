package ru.otus.hw.services;

import org.assertj.core.api.AssertionsForInterfaceTypes;
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
import ru.otus.hw.repositories.GenreRepository;

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
@DisplayName("Интеграционные тесты AuthorService")
public class AuthorServiceIntegrationTest {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private BookDto book1;
    private BookDto book2;

    String author1Id;
    String author2Id;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("comments");

        AuthorDto author1 = authorService.insert("Лев Толстой");
        AuthorDto author2 = authorService.insert("Федор Достоевский");

        GenreDto genre = genreService.insert("историческая проза");

        author1Id = author1.id();
        author2Id = author2.id();

        book1 = bookService.insert("Война и мир", author1Id, genre.id());
        book2 = bookService.insert("Анна Каренина", author2Id, genre.id());

        commentService.insert(book1.id(), "Комментарий 1");
        commentService.insert(book1.id(), "Комментарий 2");
        commentService.insert(book2.id(), "Комментарий 3");
    }

    @Test
    @DisplayName("Найти автора по айди")
    public void shouldReturnAuthorById() {
        AuthorDto actualAuthor = authorService.findById(author1Id);

        assertThat(actualAuthor.fullName()).isEqualTo("Лев Толстой");
    }

    @Test
    @DisplayName("Найти всех авторов")
    public void shouldReturnAllAuthor() {
        List<AuthorDto> actualAuthors = authorService.findAll();

        assertThat(actualAuthors)
                .isNotNull()
                .hasSize(2);

        assertThat(actualAuthors)
                .extracting(AuthorDto::fullName)
                .containsExactlyInAnyOrder("Лев Толстой", "Федор Достоевский");
    }

    @Test
    @DisplayName("Должен создавать нового автора")
    void shouldInsertAuthor() {
        List<AuthorDto> beforeAuthors = authorService.findAll();
        AssertionsForInterfaceTypes.assertThat(beforeAuthors).hasSize(2);

        AuthorDto newAuthor = authorService.insert("Новый автор");

        assertThat(newAuthor).isNotNull();
        assertThat(newAuthor.id()).isNotBlank();
        assertThat(newAuthor.fullName()).isEqualTo("Новый автор");

        List<AuthorDto> afterAuthors = authorService.findAll();

        assertThat(afterAuthors).hasSize(3);
        assertThat(afterAuthors).extracting(AuthorDto::fullName)
                .containsExactlyInAnyOrder("Новый автор", "Лев Толстой", "Федор Достоевский");
    }

    @Test
    @DisplayName("Должен обновлять автора")
    void shouldUpdateAuthor() {
        String updatedName = "обновленный автор";

        AuthorDto updatedAuthor = authorService.update(author1Id, updatedName);

        assertThat(updatedAuthor).isNotNull();
        assertThat(updatedAuthor.id()).isEqualTo(author1Id);
        assertThat(updatedAuthor.fullName()).isEqualTo(updatedName);

        AuthorDto foundAuthor = authorService.findById(author1Id);
        assertThat(foundAuthor.fullName()).isEqualTo(updatedName);
    }

    @Test
    @DisplayName("Должен удалять автора и все связанные с ним книги и комментарии")
    void shouldDeleteAuthor() {
        assertThat(bookService.findByAuthorId(author1Id)).hasSize(1);
        assertThat(commentService.findByBookId(book1.id())).hasSize(2);
        assertThat(commentService.findByBookId(book2.id())).hasSize(1);
        assertThat(authorService.findById(author1Id)).isNotNull();

        authorService.delete(author1Id);

        assertThatThrownBy(() -> authorService.findById(author1Id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Author with id=" + author1Id + " not found");

        assertThatThrownBy(() -> bookService.findById(book1.id()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id=" + book1.id() + " not found");

        assertThat(commentService.findByBookId(book1.id())).isEmpty();

        assertThat(authorService.findById(author2Id)).isNotNull();
        assertThat(authorService.findAll()).hasSize(1);
    }
}
