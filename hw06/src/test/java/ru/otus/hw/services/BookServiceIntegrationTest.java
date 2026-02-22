package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.hw.dto.BookDetailsDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@DisplayName("Интеграционные тесты BookService")
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен загружать книгу с автором, жанром без LazyInitializationException")
    void shouldReturnBookById() {
        BookDto actualBookDto = bookService.findById(100L);

        assertThat(actualBookDto.title()).isEqualTo("BookTitle_1");
        assertThat(actualBookDto.author().fullName()).isEqualTo("Author_1");
        assertThat(actualBookDto.genre().name()).isEqualTo("Genre_1");
    }

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен загружать книгу с автором, жанром и комментариями без LazyInitializationException")
    void shouldReturnBookWithComments() {
        BookDetailsDto actualBookDto = bookService.findBookWithComments(100L);

        assertThat(actualBookDto.title()).isEqualTo("BookTitle_1");
        assertThat(actualBookDto.author().fullName()).isEqualTo("Author_1");
        assertThat(actualBookDto.genre().name()).isEqualTo("Genre_1");
        assertThat(actualBookDto.commentDtos())
                .isNotNull()
                .hasSize(2);
        assertThat(actualBookDto.commentDtos())
                .extracting(CommentDto::body)
                .containsExactlyInAnyOrder("comment_1_1", "comment_1_2");
    }

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен добавлять книгу")
    void shouldInsertBook() {
        BookDto actualBookDto = bookService.insert("New Book", 1L,1L);

        assertThat(actualBookDto.id()).isGreaterThan(0);
        assertThat(actualBookDto.title()).isEqualTo("New Book");

        assertThat(actualBookDto.author())
                .isNotNull()
                .satisfies(author -> {
                    assertThat(author.id()).isEqualTo(1L);
                    assertThat(author.fullName()).isEqualTo("Author_1");
                });

        assertThat(actualBookDto.genre())
                .isNotNull()
                .satisfies(genre -> {
                    assertThat(genre.id()).isEqualTo(1L);
                    assertThat(genre.name()).isEqualTo("Genre_1");
                });
    }

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен изменять книгу")
    void shouldUpdateBook() {
        BookDto actualBookDto = bookService.update(100L, "Updated Title", 2L, 2L);

        assertThat(actualBookDto.id()).isEqualTo(100L);
        assertThat(actualBookDto.title()).isEqualTo("Updated Title");

        assertThat(actualBookDto.author())
                .isNotNull()
                .satisfies(author -> {
                    assertThat(author.id()).isEqualTo(2L);
                    assertThat(author.fullName()).isEqualTo("Author_2");
                });

        assertThat(actualBookDto.genre())
                .isNotNull()
                .satisfies(genre -> {
                    assertThat(genre.id()).isEqualTo(2L);
                    assertThat(genre.name()).isEqualTo("Genre_2");
                });
    }

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен удалять книгу и ее комментарии")
    void shouldDeleteBook() {
        long id = 100L;
        BookDto beforeDeleteBookDto = bookService.findById(id);
        assertThat(beforeDeleteBookDto).isNotNull();

        bookService.deleteById(100L);

        assertThatThrownBy(() -> bookService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id=" + id + " not found");

        List<CommentDto> commentDtos = commentService.findByBookId(id);
        assertThat(commentDtos).isEmpty();

    }

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Не должен выбрасывать исключение при удалении несуществующей книги")
    void deletingNonExistentBook() {
        long id = 999L;
        assertThatThrownBy(() -> bookService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id=" + id + " not found");
        bookService.deleteById(999L);
        assertThat(bookService.findAll()).hasSize(3);
    }

}
