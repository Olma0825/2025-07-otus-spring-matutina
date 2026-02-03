package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с книгами ")
@DataJpaTest
@Import({BookRepositoryImpl.class})
class BookRepositoryImplTest {

    @Autowired
    private BookRepositoryImpl bookRepository;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        em.flush();
        em.clear();
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        Book expectedBook = dbBooks.get(0);
        var actualBook = bookRepository.findById(expectedBook.getId());

        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
        assertThat(actualBook.get().getTitle()).isEqualTo(expectedBook.getTitle());
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = new Book(0, "BookTitle_10500", dbAuthors.get(0), dbGenres.get(0));
        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(bookRepository.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = dbBooks.get(0);
        expectedBook.setTitle("New name");
        expectedBook.setAuthor(dbAuthors.get(1));
        expectedBook.setGenre(dbGenres.get(1));

        Book savedBook = bookRepository.save(expectedBook);
        em.flush();
        em.clear();

        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());

        assertThat(foundBook)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("author", "genre")
                .isEqualTo(expectedBook);
        assertThat(foundBook.get().getAuthor()).isEqualTo(dbAuthors.get(1));
        assertThat(foundBook.get().getGenre()).isEqualTo(dbGenres.get(1));

    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        System.out.println(dbBooks);
        Author author = new Author();
        author.setFullName("Test Author");
        em.persist(author);

        Genre genre = new Genre();
        genre.setName("Test Genre");
        em.persist(genre);

        Book book = new Book();
        book.setTitle("Test Book for Delete");
        book.setAuthor(author);
        book.setGenre(genre);
        em.persist(book);

        em.flush();
        em.clear();

        long bookId = book.getId();
        assertThat(bookRepository.findById(bookId)).isPresent();
        bookRepository.deleteById(bookId);
        assertThat(bookRepository.findById(bookId)).isEmpty();
    }

    private List<Author> getDbAuthors() {
        return IntStream.range(0, 3).boxed()
                .map(a -> {
                    Author author = new Author("Author_" + a + 1);
                    em.persist(author);
                    return author;
                })
                .toList();
    }

    private List<Genre> getDbGenres() {
        return IntStream.range(0, 3).boxed()
                .map(id -> {
                    Genre genre = new Genre("Genre_" + id + 1);
                    em.persist(genre);
                    return genre;
                })
                .toList();
    }

    private List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(0, 3).boxed()
                .map(id -> {
                    Book book = new Book();
                    book.setTitle("BookTitle_" + id + 1);
                    book.setAuthor(dbAuthors.get(id));
                    book.setGenre(dbGenres.get(id));
                    em.persist(book);
                    return book;
                })
                .toList();
    }

}