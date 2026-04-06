package ru.otus.hw.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

@ChangeLog
public class InitCollectionsChangeUnit {

    private String author1Id;

    private String author2Id;

    private String author3Id;

    private String genre1Id;

    private String genre2Id;

    private String genre3Id;

    private String book1Id;

    private String book2Id;

    public InitCollectionsChangeUnit() {
    }

    @ChangeSet(order = "001", id = "createCollections", author = "developer", runAlways = true)
    public void createCollections(MongockTemplate mongockTemplate) {

        mongockTemplate.dropCollection("authors");
        mongockTemplate.dropCollection("genres");
        mongockTemplate.dropCollection("books");
        mongockTemplate.dropCollection("comments");

        mongockTemplate.createCollection("authors");
        mongockTemplate.createCollection("genres");
        mongockTemplate.createCollection("books");
        mongockTemplate.createCollection("comments");
    }

    @ChangeSet(order = "002", id = "addAuthors", author = "developer", runAlways = true)
    public void addAuthors(AuthorRepository authorRepository) {

        Author author1 = saveAuthor(authorRepository, "Лев Толстой");
        Author author2 = saveAuthor(authorRepository, "Федор Достоевский");
        Author author3 = saveAuthor(authorRepository, "Александр Пушкин");

        author1Id = author1.getId();
        author2Id = author2.getId();
        author3Id = author3.getId();
    }

    private Author saveAuthor(AuthorRepository repository, String fullName) {
        return repository.save(new Author(fullName));
    }

    @ChangeSet(order = "003", id = "addGenres", author = "developer", runAlways = true)
    public void addGenres(GenreRepository genreRepository) {

        Genre genre1 = saveGenre(genreRepository, "лирика");
        Genre genre2 = saveGenre(genreRepository, "историческая проза");
        Genre genre3 = saveGenre(genreRepository, "классическая проза");

        genre1Id = genre1.getId();
        genre2Id = genre2.getId();
        genre3Id = genre3.getId();
    }

    private Genre saveGenre(GenreRepository repository, String name) {
        return repository.save(new Genre(name));
    }

    @ChangeSet(order = "004", id = "prepareBooksData", author = "developer", runAlways = true)
    public void prepareBooksData(
            AuthorRepository authorRepository,
            GenreRepository genreRepository) {

        checkAuthorsExist(authorRepository);
        checkGenresExist(genreRepository);
    }

    private void checkAuthorsExist(AuthorRepository repository) {
        if (author1Id == null || author2Id == null || author3Id == null) {
            throw new IllegalStateException("Авторы не найдены. Шаг addAuthors должен быть выполнен ранее.");
        }
    }

    private void checkGenresExist(GenreRepository repository) {
        if (genre1Id == null || genre2Id == null || genre3Id == null) {
            throw new IllegalStateException("Жанры не найдены. Шаг addGenres должен быть выполнен ранее.");
        }
    }

    @ChangeSet(order = "005", id = "addFirstBook", author = "developer", runAlways = true)
    public void addFirstBook(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            GenreRepository genreRepository) {

        Author author3 = authorRepository.findById(author3Id).orElseThrow();
        Genre genre3 = genreRepository.findById(genre3Id).orElseThrow();

        Book book1 = bookRepository.save(new Book("Капитанская дочка", author3, genre3));
        book1Id = book1.getId();
    }

    @ChangeSet(order = "006", id = "addSecondBook", author = "developer", runAlways = true)
    public void addSecondBook(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            GenreRepository genreRepository) {

        Author author1 = authorRepository.findById(author1Id).orElseThrow();
        Genre genre2 = genreRepository.findById(genre2Id).orElseThrow();

        Book book2 = bookRepository.save(new Book("Война и мир", author1, genre2));
        book2Id = book2.getId();

    }

    @ChangeSet(order = "007", id = "addThirdBook", author = "developer", runAlways = true)
    public void addThirdBook(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            GenreRepository genreRepository) {

        Author author2 = authorRepository.findById(author2Id).orElseThrow();
        Genre genre3 = genreRepository.findById(genre3Id).orElseThrow();

        Book book3 = bookRepository.save(new Book("Преступление и наказание", author2, genre3));
    }

    @ChangeSet(order = "008", id = "addComments", author = "developer", runAlways = true)
    public void addComments(
            MongockTemplate mongockTemplate,
            BookRepository bookRepository) {

        Book book1 = bookRepository.findById(book1Id).orElseThrow();
        Book book2 = bookRepository.findById(book2Id).orElseThrow();

        saveComment(mongockTemplate, "Комментарий 1", book1);
        saveComment(mongockTemplate, "Комментарий 2", book1);
        saveComment(mongockTemplate, "Комментарий 3", book1);
        saveComment(mongockTemplate, "Комментарий 3 for book2", book2);
    }

    private void saveComment(MongockTemplate template, String text, Book book) {
        template.insert(new Comment(text, book), "comments");
    }
}
