package ru.otus.hw.services;

import org.assertj.core.api.AssertionsForInterfaceTypes;
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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import({
        AuthorServiceImpl.class,
})
@DisplayName("Интеграционные тесты AuthorService")
public class AuthorServiceIntegrationTest {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private MongoTemplate mongoTemplate;

    String author1Id;
    String author2Id;
    String author3Id;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("comments");

        Author author1 = mongoTemplate.insert(new Author("Лев Толстой"), "authors");
        Author author2 = mongoTemplate.insert(new Author("Федор Достоевский"), "authors");
        Author author3 = mongoTemplate.insert(new Author("Сергей Есенин"), "authors");

        author1Id = author1.getId();
        author2Id = author2.getId();
        author3Id = author3.getId();

        Genre genre = mongoTemplate.insert(new Genre("историческая проза"), "genres");

        Book book1 = mongoTemplate.insert(new Book("Война и мир", author1, genre), "books");
        Book book2 = mongoTemplate.insert(new Book("Анна Каренина", author1, genre), "books");

    }

    @Test
    @DisplayName("Найти автора по айди")
    public void shouldReturnAuthorById() {
        AuthorDto actualAuthor = authorService.findById(author1Id);

        assertThat(actualAuthor.fullName()).isEqualTo("Лев Толстой");
    }

    @Test
    @DisplayName("Найти всех авторов")
    public void shouldReturnAllAuthor() {
        List<AuthorDto> actualAuthors = authorService.findAll();

        assertThat(actualAuthors)
                .isNotNull()
                .hasSize(3);

        assertThat(actualAuthors)
                .extracting(AuthorDto::fullName)
                .containsExactlyInAnyOrder("Лев Толстой", "Федор Достоевский", "Сергей Есенин");
    }

    @Test
    @DisplayName("Должен создавать нового автора")
    void shouldInsertAuthor() {
        List<AuthorDto> beforeAuthors = authorService.findAll();
        AssertionsForInterfaceTypes.assertThat(beforeAuthors).hasSize(3);

        AuthorDto newAuthor = authorService.insert("Новый автор");

        assertThat(newAuthor).isNotNull();
        assertThat(newAuthor.id()).isNotBlank();
        assertThat(newAuthor.fullName()).isEqualTo("Новый автор");

        List<AuthorDto> afterAuthors = authorService.findAll();

        assertThat(afterAuthors).hasSize(4);
        assertThat(afterAuthors).extracting(AuthorDto::fullName)
                .containsExactlyInAnyOrder("Новый автор", "Лев Толстой", "Федор Достоевский", "Сергей Есенин");
    }

    @Test
    @DisplayName("Должен обновлять автора")
    void shouldUpdateAuthor() {
        String updatedName = "обновленный автор";

        AuthorDto updatedAuthor = authorService.update(author1Id, updatedName);

        assertThat(updatedAuthor).isNotNull();
        assertThat(updatedAuthor.id()).isEqualTo(author1Id);
        assertThat(updatedAuthor.fullName()).isEqualTo(updatedName);

        AuthorDto foundAuthor = authorService.findById(author1Id);
        assertThat(foundAuthor.fullName()).isEqualTo(updatedName);
    }

    @Test
    @DisplayName("Должен удалять автора без книг")
    void shouldDeleteAuthor() {
        long booksCount = mongoTemplate.count(
                new Query(Criteria.where("author.$id").is(author3Id)),
                Book.class
        );
        assertThat(booksCount).isZero();

        AuthorDto foundAuthor = authorService.findById(author3Id);
        assertThat(foundAuthor).isNotNull();
        assertThat(foundAuthor.fullName()).isEqualTo("Сергей Есенин");

        authorService.delete(author3Id);

        assertThatThrownBy(() -> authorService.findById(author3Id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Author with id=" + author3Id + " not found");

    }

    @Test
    @DisplayName("Должен вызывать исключение при удалении автора, у которого есть книги")
    void shouldRaiseExceptionByDeleteAuthor() {
        ObjectId objectId = new ObjectId(author1Id);
        long booksCount = mongoTemplate.count(
                new Query(Criteria.where("author.$id").is(objectId)),
                Book.class
        );
        assertThat(booksCount).isGreaterThan(0);
        assertThat(booksCount).isEqualTo(2);

        AuthorDto foundAuthor = authorService.findById(author1Id);

        assertThat(foundAuthor).isNotNull();
        assertThat(foundAuthor.fullName()).isEqualTo("Лев Толстой");

        assertThatThrownBy(() -> authorService.delete(author1Id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("You can't delete an author from id = " + author1Id
                        + " because there are books of this author.");

        assertThat(authorService.findById(author1Id)).isNotNull();
    }

}
