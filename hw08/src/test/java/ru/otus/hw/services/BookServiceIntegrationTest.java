package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.*;
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
@DisplayName("Интеграционные тесты BookService")
public class BookServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookService bookService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private AuthorDto author;
    private GenreDto genre;
    private AuthorDto author2;
    private GenreDto genre2;
    private String bookId;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("comments");

        author = authorService.insert("Лев Толстой");
        genre = genreService.insert("классическая проза");
        author2 = authorService.insert("Федор Достоевский");
        genre2 = genreService.insert("историческая проза");
        BookDto book = bookService.insert("Война и мир", author.id(), genre.id());
        bookId = book.id();
        CommentDto comment1 = commentService.insert(book.id(), "Тестовый комментарий 1");
        CommentDto comment2 = commentService.insert(book.id(), "Тестовый комментарий 2");
    }


    @Test
    @DisplayName("Должен загружать книгу с автором, жанром без LazyInitializationException")
    void shouldReturnBookById() {
        BookDto actualBookDto = bookService.findById(bookId);

        assertThat(actualBookDto.title()).isEqualTo("Война и мир");
        assertThat(actualBookDto.author().fullName()).isEqualTo("Лев Толстой");
        assertThat(actualBookDto.genre().name()).isEqualTo("классическая проза");
    }

    @Test
    @DisplayName("Должен загружать книгу с автором, жанром и комментариями без LazyInitializationException")
    void shouldReturnBookWithComments() {
        BookDetailsDto actualBookDto = bookService.findBookByIdWithComments(bookId);

        assertThat(actualBookDto.title()).isEqualTo("Война и мир");
        assertThat(actualBookDto.author().fullName()).isEqualTo("Лев Толстой");
        assertThat(actualBookDto.genre().name()).isEqualTo("классическая проза");
        assertThat(actualBookDto.commentDtos())
                .isNotNull()
                .hasSize(2);
        assertThat(actualBookDto.commentDtos())
                .extracting(CommentDto::body)
                .containsExactlyInAnyOrder("Тестовый комментарий 1", "Тестовый комментарий 2");
    }

    @Test
    @DisplayName("Должен находить все книги")
    void shouldFindAllBooks() {
        List<BookDto> books = bookService.findAll();

        assertThat(books).hasSize(1);

        BookDto foundBook = books.get(0);
        assertThat(foundBook.title()).isEqualTo("Война и мир");
        assertThat(foundBook.author().fullName()).isEqualTo("Лев Толстой");
    }

    @Test
    @DisplayName("Должен добавлять книгу")
    void shouldInsertBook() {
        BookDto actualBookDto = bookService.insert("New Book", author.id(),genre.id());

        assertThat(actualBookDto.title()).isEqualTo("New Book");

        assertThat(actualBookDto.author())
                .isNotNull()
                .satisfies(actualAuthor -> {
                    assertThat(actualAuthor.id()).isEqualTo(author.id());
                    assertThat(actualAuthor.fullName()).isEqualTo("Лев Толстой");
                });

        assertThat(actualBookDto.genre())
                .isNotNull()
                .satisfies(actualGenre -> {
                    assertThat(actualGenre.id()).isEqualTo(genre.id());
                    assertThat(actualGenre.name()).isEqualTo("классическая проза");
                });
    }

    @Test
    @DisplayName("Должен изменять книгу")
    void shouldUpdateBook() {
        BookDto actualBookDto = bookService.update(bookId, "Updated Title", author2.id(), genre2.id());

        assertThat(actualBookDto.id()).isEqualTo(bookId);
        assertThat(actualBookDto.title()).isEqualTo("Updated Title");

        assertThat(actualBookDto.author())
                .isNotNull()
                .satisfies(actualAuthor -> {
                    assertThat(actualAuthor.id()).isEqualTo(author2.id());
                    assertThat(actualAuthor.fullName()).isEqualTo("Федор Достоевский");
                });

        assertThat(actualBookDto.genre())
                .isNotNull()
                .satisfies(actualGenre -> {
                    assertThat(actualGenre.id()).isEqualTo(genre2.id());
                    assertThat(actualGenre.name()).isEqualTo("историческая проза");
                });
    }

    @Test
    @DisplayName("Должен удалять книгу и ее комментарии")
    void shouldDeleteBook() {
        BookDto beforeDeleteBookDto = bookService.findById(bookId);
        assertThat(beforeDeleteBookDto).isNotNull();

        bookService.deleteById(bookId);

        assertThatThrownBy(() -> bookService.findById(bookId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id=" + bookId + " not found");

        List<CommentDto> commentDtos = commentService.findByBookId(bookId);
        assertThat(commentDtos).isEmpty();

    }

    @Test
    @DisplayName("Не должен выбрасывать исключение при удалении несуществующей книги")
    void deletingNonExistentBook() {
        String fakeBookId = "000000000000000000000000";
        assertThatThrownBy(() -> bookService.findById(fakeBookId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id=" + fakeBookId + " not found");
        bookService.deleteById(fakeBookId);
        assertThat(bookService.findAll()).hasSize(1);
    }

}
