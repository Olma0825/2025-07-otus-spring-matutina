package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class CommentConverter {
    public String commentToString (CommentDto comment) {
        return "Id: %d, body: %s, createdAt: {%s}"
                .formatted(comment.id()
                ,comment.body()
                ,formatDateTime(comment.createdAt())
               );
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ?
                dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) :
                "null";
    }
}
