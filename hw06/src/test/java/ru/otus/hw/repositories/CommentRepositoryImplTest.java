package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CommentRepositoryImpl.class)
@DisplayName("Репозиторий для работы с комментариями")
public class CommentRepositoryImplTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepositoryImpl repository;

    List<Comment> commentList;

    Book book;

    @BeforeEach
    void setUp() {
        Author author = new Author();
        author.setFullName("Лев Толстой");
        em.persist(author);

        Genre genre = new Genre();
        genre.setName("Роман-эпопея");
        em.persist(genre);

        book = new Book();
        book.setTitle("Война и мир");
        book.setGenre(genre);
        book.setAuthor(author);
        em.persist(book);

        commentList = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setBody("Комментарий 1");
        comment1.setBook(book);
        commentList.add(comment1);
        Comment comment2 = new Comment();
        comment2.setBody("Комментарий 2");
        comment2.setBook(book);
        commentList.add(comment2);
        em.persist(comment1);
        em.persist(comment2);

        book.setComments(commentList);

        em.flush();
    }

    @Test
    @DisplayName("должен загружать комментарий по Id")
    void shouldReturnCommentById() {
        Comment expectedComment = commentList.get(0);
        Optional<Comment> actualComment = repository.findById(expectedComment.getId());

        assertThat(actualComment).isPresent();
        assertThat(actualComment.get().getBody()).isEqualTo("Комментарий 1");
        assertThat(actualComment.get().getBook().getId()).isEqualTo(expectedComment.getBook().getId());
    }

    @Test
    @DisplayName("должен находить все комментарии по Id книги")
    void shouldFindCommentsByBookId() {
        List<Comment> expectedCommentList = new ArrayList<>(commentList);

        List<Comment> actualCommentList = repository.findByBookId(book.getId());

        assertThat(actualCommentList).hasSize(expectedCommentList.size());
        assertThat(actualCommentList).containsExactlyElementsOf(expectedCommentList);
    }

    @Test
    @DisplayName("должен сохранять новый комментарий для книги")
    void shouldSaveComment() {
        Comment comment = new Comment("Новый комментарий", book);
        Comment savedComment = repository.save(comment);

        assertThat(savedComment.getId()).isGreaterThan(0);
        assertThat(savedComment.getBody()).isEqualTo(comment.getBody());
        assertThat(savedComment.getBook()).isEqualTo(book);
        assertThat(savedComment.getCreatedAt()).isNotNull();
        assertThat(savedComment.getCreatedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Должен удалить комментарий")
    @Transactional
    void shouldRemoveComment() {
        Comment comment = commentList.get(0);
        repository.delete(comment.getId());

        em.flush();
        em.clear();

        Comment found = em.find(Comment.class, comment.getId());
        assertThat(found).isNull();

        Book bookFromDb = em.find(Book.class, book.getId());
        assertThat(bookFromDb).isNotNull();
        assertThat(bookFromDb.getComments()).hasSize(1);
    }
}
