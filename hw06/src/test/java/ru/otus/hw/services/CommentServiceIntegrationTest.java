package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@DisplayName("Интеграционные тесты CommentService")
public class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookService bookService;

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен загружать комментарий без LazyInitializationException")
    void shouldReturnCommentById() {
        CommentDto commentDto = commentService.findById(1000L);

        assertThat(commentDto.id()).isEqualTo(1000L);
        assertThat(commentDto.body()).isEqualTo("comment_1_1");
        assertThat(commentDto.createdAt()).isNotNull();
    }

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен загружать все комментарии по id книги")
    void shouldReturnCommentByBookId() {
        List<CommentDto> commentDtos = commentService.findByBookId(100L);

        assertThat(commentDtos)
                .isNotNull()
                .hasSize(2);

        assertThat(commentDtos)
                .extracting(CommentDto::id)
                .containsExactlyInAnyOrder(1000L, 1001L);

        assertThat(commentDtos)
                .extracting(CommentDto::body)
                .containsExactlyInAnyOrder("comment_1_1", "comment_1_2");
    }

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен сохранить новый комментарий")
    void shouldSaveComment() {

        List<CommentDto> beforeCommentDtos = commentService.findByBookId(100L);
        assertThat(beforeCommentDtos).hasSize(2);

        CommentDto commentDto = commentService.insert(100L, "New comment");

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.id()).isGreaterThan(0);
        assertThat(commentDto.body()).isEqualTo("New comment");
        assertThat(commentDto.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());

        List<CommentDto> afterCommentDtos = commentService.findByBookId(100L);
        assertThat(afterCommentDtos).hasSize(3);
        assertThat(afterCommentDtos).extracting(CommentDto::body).contains("New comment");
    }

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен изменять комментарий")
    void shouldUpdateComment() {

        List<CommentDto> beforeCommentDtos = commentService.findByBookId(100L);
        long commentId = beforeCommentDtos.get(0).id();
        assertThat(beforeCommentDtos).hasSize(2);

        CommentDto commentDto = commentService.update(commentId, "Updated comment");

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.id()).isEqualTo(commentId);
        assertThat(commentDto.body()).isEqualTo("Updated comment");
        assertThat(commentDto.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());

        List<CommentDto> afterCommentDtos = commentService.findByBookId(100L);
        assertThat(afterCommentDtos).hasSize(2);
        assertThat(afterCommentDtos).extracting(CommentDto::body).contains("Updated comment");
    }

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен удалять комментарий")
    void shouldDeleteComment() {

        long commentId = 1000L;
        CommentDto beforeDeleteCommentDto = commentService.findById(commentId);
        assertThat(beforeDeleteCommentDto).isNotNull();

        commentService.delete(commentId);

        assertThatThrownBy(() -> commentService.findById(commentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment with id=" + commentId + " not found");
    }

}
