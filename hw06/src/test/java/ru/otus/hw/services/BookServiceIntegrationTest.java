package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDetailsDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("Интеграционные тесты BookService")
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @PersistenceContext
    private EntityManager em;

    private Book book;

    @BeforeEach
    void setUp() {
        Genre genre = new Genre();
        genre.setName("Новый жанр");
        em.persist(genre);

        Author author = new Author();
        author.setFullName("Новый автор");
        em.persist(author);

        book = new Book();
        book.setTitle("Название книги");
        book.setGenre(genre);
        book.setAuthor(author);
        book.addComment("Комментарий 1");
        book.addComment("Комментарий 2");
        em.persist(book);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("Должен загружать книгу с автором, жанром без LazyInitializationException")
    void shouldReturnBookById() {
        BookDto actualBookDto = bookService.findById(book.getId());

        assertThat(actualBookDto.title()).isEqualTo("Название книги");
        assertThat(actualBookDto.author().getFullName()).isEqualTo("Новый автор");
        assertThat(actualBookDto.genre().getName()).isEqualTo("Новый жанр");
    }

    @Test
    @DisplayName("Должен загружать книгу с автором, жанром и комментариями без LazyInitializationException")
    void shouldReturnBookWithComments() {
        BookDetailsDto actualBookDto = bookService.findBookWithComments(book.getId());

        assertThat(actualBookDto.title()).isEqualTo("Название книги");
        assertThat(actualBookDto.author().getFullName()).isEqualTo("Новый автор");
        assertThat(actualBookDto.genre().getName()).isEqualTo("Новый жанр");
        assertThat(actualBookDto.comments())
                .isNotNull()
                .hasSize(2);
        assertThat(actualBookDto.comments())
                .extracting(CommentDto::body)
                .containsExactlyInAnyOrder("Комментарий 1", "Комментарий 2");
    }

}
