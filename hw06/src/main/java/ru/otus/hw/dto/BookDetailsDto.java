package ru.otus.hw.dto;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

public record BookDetailsDto(

    long id,

    String title,

    Author author,

    Genre genre,

    List<CommentDto> comments
) {
    public static BookDetailsDto toDto(Book book) {

        if (book == null) {
            return null;
        }

        List<CommentDto> commentDtos = book.getComments().stream()
                .map(CommentDto::toDto)
                .toList();

        return new BookDetailsDto(book.getId(), book.getTitle(), book.getAuthor(), book.getGenre()
                , commentDtos);
    }
}
