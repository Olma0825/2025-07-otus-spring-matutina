package ru.otus.hw.dto;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

public record BookDetailsDto(

    long id,

    String title,

    AuthorDto author,

    GenreDto genre,

    List<CommentDto> commentDtos
) {
    public static BookDetailsDto toDto(Book book, List<Comment> comments) {

        if (book == null) {
            return null;
        }

        return new BookDetailsDto(book.getId(), book.getTitle(), AuthorDto.toDto(book.getAuthor()),
                GenreDto.toDto(book.getGenre()),
                comments.stream().map(CommentDto::toDto).toList());
    }
}
