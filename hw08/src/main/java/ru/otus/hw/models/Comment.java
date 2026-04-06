package ru.otus.hw.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "comments")
@Data
public class Comment {

    @Id
    private String id;

    private String body;

    private LocalDateTime createdAt;

    @DBRef(lazy = true)
    private Book book;

    public Comment() {
    }

    public Comment(String body, Book book) {
        this.body = body;
        this.book = book;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", createdAt=" + createdAt +
        //", bookId=" + book.getId() +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Comment comment = (Comment) object;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
