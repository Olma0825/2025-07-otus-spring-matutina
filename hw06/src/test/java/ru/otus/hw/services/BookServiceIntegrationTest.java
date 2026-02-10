package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.hw.dto.BookDetailsDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Интеграционные тесты BookService")
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен загружать книгу с автором, жанром без LazyInitializationException")
    void shouldReturnBookById() {
        BookDto actualBookDto = bookService.findById(100L);

        assertThat(actualBookDto.title()).isEqualTo("BookTitle_1");
        assertThat(actualBookDto.author().getFullName()).isEqualTo("Author_1");
        assertThat(actualBookDto.genre().getName()).isEqualTo("Genre_1");
    }

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен загружать книгу с автором, жанром и комментариями без LazyInitializationException")
    void shouldReturnBookWithComments() {
        BookDetailsDto actualBookDto = bookService.findBookWithComments(100L);

        assertThat(actualBookDto.title()).isEqualTo("BookTitle_1");
        assertThat(actualBookDto.author().getFullName()).isEqualTo("Author_1");
        assertThat(actualBookDto.genre().getName()).isEqualTo("Genre_1");
        assertThat(actualBookDto.commentDtos())
                .isNotNull()
                .hasSize(2);
        assertThat(actualBookDto.commentDtos())
                .extracting(CommentDto::body)
                .containsExactlyInAnyOrder("comment_1_1", "comment_1_2");
    }

}
