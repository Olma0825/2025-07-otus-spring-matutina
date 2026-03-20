package ru.otus.hw.repositories;

import java.util.List;

public interface BookRepositoryCustom {
    void deleteAllByAuthorId(String authorId);

    void deleteAllByGenreId(String genreId);

    List<String> findIdsByAuthorId(String authorId);

    List<String> findIdsByGenreId(String genreId);
}
