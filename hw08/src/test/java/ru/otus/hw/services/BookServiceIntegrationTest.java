package ru.otus.hw.services;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.dto.*;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import({
        BookServiceImpl.class,
})
@DisplayName("Интеграционные тесты BookService")
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Author author2;
    private Genre genre2;
    private String bookId;
    private String authorId;
    private String genreId;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("comments");

        Author author = mongoTemplate.insert(new Author("Лев Толстой"), "authors");
        Genre genre = mongoTemplate.insert(new Genre("классическая проза"), "genres");
        authorId = author.getId();
        genreId = genre.getId();
        author2 = mongoTemplate.insert(new Author("Федор Достоевский"), "authors");
        genre2 = mongoTemplate.insert(new Genre("историческая проза"), "genres");
        Book book = mongoTemplate.insert(new Book("Война и мир", author, genre), "books");
        bookId = book.getId();
        Comment comment1 = mongoTemplate.insert(new Comment("Тестовый комментарий 1", book), "comments");
        Comment comment2 = mongoTemplate.insert(new Comment("Тестовый комментарий 2", book), "comments");
    }


    @Test
    @DisplayName("Должен загружать книгу с автором, жанром без LazyInitializationException")
    void shouldReturnBookById() {
        BookDto actualBookDto = bookService.findById(bookId);

        assertThat(actualBookDto.title()).isEqualTo("Война и мир");
        assertThat(actualBookDto.author().fullName()).isEqualTo("Лев Толстой");
        assertThat(actualBookDto.genre().name()).isEqualTo("классическая проза");
    }

    @Test
    @DisplayName("Должен загружать книгу с автором, жанром и комментариями без LazyInitializationException")
    void shouldReturnBookWithComments() {
        BookDetailsDto actualBookDto = bookService.findBookByIdWithComments(bookId);

        assertThat(actualBookDto.title()).isEqualTo("Война и мир");
        assertThat(actualBookDto.author().fullName()).isEqualTo("Лев Толстой");
        assertThat(actualBookDto.genre().name()).isEqualTo("классическая проза");
        assertThat(actualBookDto.commentDtos())
                .isNotNull()
                .hasSize(2);
        assertThat(actualBookDto.commentDtos())
                .extracting(CommentDto::body)
                .containsExactlyInAnyOrder("Тестовый комментарий 1", "Тестовый комментарий 2");
    }

    @Test
    @DisplayName("Должен находить все книги")
    void shouldFindAllBooks() {
        List<BookDto> books = bookService.findAll();

        assertThat(books).hasSize(1);

        BookDto foundBook = books.get(0);
        assertThat(foundBook.title()).isEqualTo("Война и мир");
        assertThat(foundBook.author().fullName()).isEqualTo("Лев Толстой");
    }

    @Test
    @DisplayName("Должен добавлять книгу")
    void shouldInsertBook() {
        BookDto actualBookDto = bookService.insert("New Book", authorId, genreId);

        assertThat(actualBookDto.title()).isEqualTo("New Book");

        assertThat(actualBookDto.author())
                .isNotNull()
                .satisfies(actualAuthor -> {
                    assertThat(actualAuthor.id()).isEqualTo(authorId);
                    assertThat(actualAuthor.fullName()).isEqualTo("Лев Толстой");
                });

        assertThat(actualBookDto.genre())
                .isNotNull()
                .satisfies(actualGenre -> {
                    assertThat(actualGenre.id()).isEqualTo(genreId);
                    assertThat(actualGenre.name()).isEqualTo("классическая проза");
                });
    }

    @Test
    @DisplayName("Должен изменять книгу")
    void shouldUpdateBook() {
        BookDto actualBookDto = bookService.update(bookId, "Updated Title", author2.getId(), genre2.getId());

        assertThat(actualBookDto.id()).isEqualTo(bookId);
        assertThat(actualBookDto.title()).isEqualTo("Updated Title");

        assertThat(actualBookDto.author())
                .isNotNull()
                .satisfies(actualAuthor -> {
                    assertThat(actualAuthor.id()).isEqualTo(author2.getId());
                    assertThat(actualAuthor.fullName()).isEqualTo("Федор Достоевский");
                });

        assertThat(actualBookDto.genre())
                .isNotNull()
                .satisfies(actualGenre -> {
                    assertThat(actualGenre.id()).isEqualTo(genre2.getId());
                    assertThat(actualGenre.name()).isEqualTo("историческая проза");
                });
    }

    @Test
    @DisplayName("Должен удалять книгу и ее комментарии")
    void shouldDeleteBook() {
        BookDto beforeDeleteBookDto = bookService.findById(bookId);
        assertThat(beforeDeleteBookDto).isNotNull();

        bookService.deleteById(bookId);

        assertThatThrownBy(() -> bookService.findById(bookId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id=" + bookId + " not found");

        //List<CommentDto> commentDtos = commentService.findByBookId(bookId);
        ObjectId objectId = new ObjectId(bookId);
        Query query = new Query(Criteria.where("book.$id").is(objectId));
        List<Comment> commentDtos = mongoTemplate.find(query, Comment.class);

        assertThat(commentDtos).isEmpty();

    }

    @Test
    @DisplayName("Не должен выбрасывать исключение при удалении несуществующей книги")
    void deletingNonExistentBook() {
        String fakeBookId = "000000000000000000000000";
        assertThatThrownBy(() -> bookService.findById(fakeBookId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id=" + fakeBookId + " not found");
        bookService.deleteById(fakeBookId);
        assertThat(bookService.findAll()).hasSize(1);
    }

}
