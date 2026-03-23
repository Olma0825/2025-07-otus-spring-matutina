package ru.otus.hw.repositories;

import java.util.List;

public interface BookRepositoryCustom {
    List<String> findIdsByAuthorId(String authorId);

    List<String> findIdsByGenreId(String genreId);
}
