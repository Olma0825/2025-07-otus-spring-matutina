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
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.*;

import java.time.LocalDateTime;
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
@DisplayName("Интеграционные тесты CommentService")
public class CommentServiceIntegrationTest {

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

    private BookDto book;
    private CommentDto comment1;
    private String bookId;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("comments");

        AuthorDto author = authorService.insert("Лев Толстой");
        GenreDto genre = genreService.insert("классическая проза");
        book = bookService.insert("Война и мир", author.id(), genre.id());
        bookId = book.id();
        comment1 = commentService.insert(book.id(), "Тестовый комментарий 1");
        CommentDto comment2 = commentService.insert(book.id(), "Тестовый комментарий 2");
    }

    @Test
    @DisplayName("Должен загружать комментарий")
    void shouldReturnCommentById() {
        CommentDto commentDto = commentService.findById(comment1.id());

        assertThat(commentDto.id()).isEqualTo(comment1.id());
        assertThat(commentDto.body()).isEqualTo("Тестовый комментарий 1");
        assertThat(commentDto.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Должен загружать все комментарии по id книги")
    void shouldReturnCommentByBookId() {
        List<CommentDto> commentDtos = commentService.findByBookId(book.id());

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

        String commentId = comment1.id();
        CommentDto beforeDeleteCommentDto = commentService.findById(commentId);
        assertThat(beforeDeleteCommentDto).isNotNull();

        commentService.delete(commentId);

        assertThatThrownBy(() -> commentService.findById(commentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment with id=" + commentId + " not found");
    }
}