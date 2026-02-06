package ru.otus.hw.dto;

import ru.otus.hw.models.Comment;

import java.time.LocalDateTime;

public record CommentDto(
        long id,
        String body,
        LocalDateTime createdAt
) {
    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return new CommentDto(comment.getId(), comment.getBody(), comment.getCreatedAt());
    }
}
