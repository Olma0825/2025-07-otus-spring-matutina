package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("Интеграционные тесты CommentService")
public class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @PersistenceContext
    private EntityManager em;

    private Comment comment1;

    @BeforeEach
    void setUp() {
        Genre genre = new Genre();
        genre.setName("Новый жанр");
        em.persist(genre);

        Author author = new Author();
        author.setFullName("Новый автор");
        em.persist(author);

        Book book = new Book();
        book.setTitle("Название книги");
        book.setGenre(genre);
        book.setAuthor(author);
        comment1 = new Comment("Комментарий 1", book);
        Comment comment2 = new Comment("Комментарий 2", book);
        book.addComment(comment1);
        book.addComment(comment2);
        em.persist(book);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("Должен загружать комментарий без LazyInitializationException")
    void shouldReturnBookById() {
        CommentDto commentDto = commentService.findById(comment1.getId());

        assertThat(commentDto.id()).isGreaterThan(0);
        assertThat(commentDto.body()).isEqualTo("Комментарий 1");
        assertThat(commentDto.createdAt()).isNotNull();
    }

}
