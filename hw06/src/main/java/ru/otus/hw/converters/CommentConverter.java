package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class CommentConverter {
    public String commentToString (Comment comment) {
        return "Id: %d, body: %s, createdAt: {%s}, bookId: [%d]"
                .formatted(comment.getId()
                ,comment.getBody()
                ,formatDateTime(comment.getCreatedAt())
                ,comment.getBook().getId());
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ?
                dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) :
                "null";
    }
}
