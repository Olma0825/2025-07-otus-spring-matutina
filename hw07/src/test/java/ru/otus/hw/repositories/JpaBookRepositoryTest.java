package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с книгами ")
@DataJpaTest
class JpaBookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

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
        assertThat(returnedBook.getId()).isGreaterThan(0);
        em.flush();
        em.clear();


        Book actualBook = em.find(Book.class, returnedBook.getId());
        assertThat(actualBook)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook1 = dbBooks.get(0);
        var expectedBook = new Book(expectedBook1.getId());
        expectedBook.setTitle("New name");
        expectedBook.setAuthor(dbAuthors.get(1));
        expectedBook.setGenre(dbGenres.get(1));

        Book savedBook = bookRepository.save(expectedBook);
        em.flush();
        em.clear();

        Book foundBook = em.find(Book.class, savedBook.getId());

        assertThat(foundBook)
                .isNotNull()
                .satisfies(book -> {
                    assertThat(book.getId()).isEqualTo(expectedBook.getId());
                    assertThat(book.getTitle()).isEqualTo(expectedBook.getTitle());
                    assertThat(book.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId());
                    assertThat(book.getGenre().getId()).isEqualTo(expectedBook.getGenre().getId());
                });

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
        Book foundBook = em.find(Book.class, bookId);
        assertThat(foundBook).isNotNull();

        bookRepository.deleteById(bookId);
        em.flush();
        em.clear();

        foundBook = em.find(Book.class, bookId);
        assertThat(foundBook).isNull();
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