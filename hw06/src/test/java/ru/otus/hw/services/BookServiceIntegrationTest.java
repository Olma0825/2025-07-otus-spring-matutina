package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Интеграционные тесты BookService")
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Book book;

    private Genre genre;

    private Author author;

    @BeforeEach
    void setUp() {
        genre = new Genre();
        genre.setName("Новый жанр");
        genreRepository.save(genre);

        author = new Author();
        author.setFullName("Новый автор");
        authorRepository.save(author);

        book = new Book();
        book.setTitle("Название книги");
        book.setGenre(genre);
        book.setAuthor(author);
        bookRepository.save(book);

        Comment comment1 = new Comment("Комментарий 1", book);
        Comment comment2 = new Comment("Комментарий 2", book);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
    }

    @Test
    @DisplayName("Должен загружать книгу с автором и жанром без LazyInitializationException")
    @Transactional
    void shouldReturnBookById() {
        Optional<Book> bookOptional = bookService.findById(book.getId());

        assertThat(bookOptional).isPresent();

        Book actualBook = bookOptional.get();

        assertThatCode(() -> {
            String authorName = actualBook.getAuthor().getFullName();
            String genreName = actualBook.getGenre().getName();

            assertThat(authorName).isEqualTo(author.getFullName());
            assertThat(genreName).isEqualTo(genre.getName());
        }).doesNotThrowAnyException();

        System.out.println("Test completed");
    }

}
