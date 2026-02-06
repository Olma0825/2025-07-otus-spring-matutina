package ru.otus.hw.dto;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

public record BookDto(

    long id,

    String title,

    Author author,

    Genre genre
) {
    public static BookDto toDto(Book book) {

        if (book == null) {
            return null;
        }

        return new BookDto(book.getId(), book.getTitle(), book.getAuthor(), book.getGenre());
    }
}
