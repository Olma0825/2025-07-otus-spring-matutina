package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> findAll();

    GenreDto findById(String id);

    GenreDto insert(String name);

    GenreDto update(String id, String name);

    void delete(String id);
}
