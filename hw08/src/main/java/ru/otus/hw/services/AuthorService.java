package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();

    AuthorDto findById(String id);

    AuthorDto insert(String fullName);

    AuthorDto update(String id, String fullName);

    void delete(String id);
}
