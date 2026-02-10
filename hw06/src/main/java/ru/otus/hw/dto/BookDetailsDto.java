package ru.otus.hw.dto;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

public record BookDetailsDto(

    long id,

    String title,

    Author author,

    Genre genre,

    List<CommentDto> commentDtos
) {
    public static BookDetailsDto toDto(Book book, List<Comment> comments) {

        if (book == null) {
            return null;
        }

        return new BookDetailsDto(book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(),
                comments.stream().map(CommentDto::toDto).toList());
    }
}
