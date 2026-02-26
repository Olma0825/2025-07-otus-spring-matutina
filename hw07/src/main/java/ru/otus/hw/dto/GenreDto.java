package ru.otus.hw.dto;

import ru.otus.hw.models.Genre;

public record GenreDto(
        long id,

        String name
) {
    public static GenreDto toDto(Genre genre) {
        if (genre == null) {
            return null;
        }

        return new GenreDto(genre.getId(), genre.getName());
    }

    public static Genre toGenre(GenreDto genreDto) {

        if (genreDto == null) {
            return null;
        }
        return new Genre(genreDto.id, genreDto.name);
    }
}
