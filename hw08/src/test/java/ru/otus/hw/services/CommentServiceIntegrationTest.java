package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import({
        CommentServiceImpl.class,
})
@DisplayName("Интеграционные тесты CommentService")
public class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private String comment1Id;
    private String bookId;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("comments");

        Author author = mongoTemplate.insert(new Author("Лев Толстой"), "authors");
        Genre genre = mongoTemplate.insert(new Genre("классическая проза"), "genres");
        Book book = mongoTemplate.insert(new Book("Война и мир", author, genre), "books");
        bookId = book.getId();
        Comment comment1 = mongoTemplate.insert(new Comment("Тестовый комментарий 1", book), "comments");
        Comment comment2 = mongoTemplate.insert(new Comment("Тестовый комментарий 2", book), "comments");
        comment1Id = comment1.getId();

    }

    @Test
    @DisplayName("Должен загружать комментарий")
    void shouldReturnCommentById() {
        CommentDto commentDto = commentService.findById(comment1Id);

        assertThat(commentDto.id()).isEqualTo(comment1Id);
        assertThat(commentDto.body()).isEqualTo("Тестовый комментарий 1");
        assertThat(commentDto.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Должен загружать все комментарии по id книги")
    void shouldReturnCommentByBookId() {
        List<CommentDto> commentDtos = commentService.findByBookId(bookId);

        assertThat(commentDtos)
                .isNotNull()
                .hasSize(2);

        assertThat(commentDtos)
                .extracting(CommentDto::body)
                .containsExactlyInAnyOrder("Тестовый комментарий 1", "Тестовый комментарий 2");
    }

    @Test
    @DisplayName("Должен сохранить новый комментарий")
    void shouldSaveComment() {
        List<CommentDto> beforeCommentDtos = commentService.findByBookId(bookId);
        assertThat(beforeCommentDtos).hasSize(2);

        CommentDto commentDto = commentService.insert(bookId,"New comment");

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.body()).isEqualTo("New comment");
        assertThat(commentDto.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());

        List<CommentDto> afterCommentDtos = commentService.findByBookId(bookId);
        assertThat(afterCommentDtos).hasSize(3);
        assertThat(afterCommentDtos).extracting(CommentDto::body).contains("New comment");
    }

    @Test
    @DisplayName("Должен изменять комментарий")
    void shouldUpdateComment() {

        List<CommentDto> beforeCommentDtos = commentService.findByBookId(bookId);
        String commentId = beforeCommentDtos.get(0).id();
        assertThat(beforeCommentDtos).hasSize(2);

        CommentDto commentDto = commentService.update(commentId, "Updated comment");

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.id()).isEqualTo(commentId);
        assertThat(commentDto.body()).isEqualTo("Updated comment");
        assertThat(commentDto.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());

        List<CommentDto> afterCommentDtos = commentService.findByBookId(bookId);
        assertThat(afterCommentDtos).hasSize(2);
        assertThat(afterCommentDtos).extracting(CommentDto::body).contains("Updated comment");
    }

    @Test
    @DisplayName("Должен удалять комментарий")
    void shouldDeleteComment() {
        CommentDto beforeDeleteCommentDto = commentService.findById(comment1Id);
        assertThat(beforeDeleteCommentDto).isNotNull();

        commentService.delete(comment1Id);

        assertThatThrownBy(() -> commentService.findById(comment1Id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment with id=" + comment1Id + " not found");
    }
}