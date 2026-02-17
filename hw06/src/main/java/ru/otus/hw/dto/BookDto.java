package ru.otus.hw.dto;

import ru.otus.hw.models.Book;

public record BookDto(

        long id,

        String title,

        AuthorDto author,

        GenreDto genre
) {
    public static BookDto toDto(Book book) {

        if (book == null) {
            return null;
        }

        return new BookDto(book.getId(), book.getTitle(), AuthorDto.toDto(book.getAuthor()),
                GenreDto.toDto(book.getGenre()));
    }

    public static Book toBook(BookDto bookDto) {
        if (bookDto == null) {
            return null;
        }

        return new Book(bookDto.id, bookDto.title, AuthorDto.toAuthor(bookDto.author),
                GenreDto.toGenre(bookDto.genre));
    }
}
