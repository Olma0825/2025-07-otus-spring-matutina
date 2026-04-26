package ru.otus.hw.dto;

import ru.otus.hw.models.Author;

public record AuthorDto(
        long id,
        String fullName
) {
    public static AuthorDto toDto(Author author) {
        if (author == null) {
            return null;
        }

        return new AuthorDto(author.getId(), author.getFullName());
    }

    public static Author toAuthor(AuthorDto authorDto) {
        if (authorDto == null) {
            return null;
        }

        return new Author(authorDto.id, authorDto.fullName);
    }
}
