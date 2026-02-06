package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> findByBookId(long bookId);

    CommentDto insert(long bookId, String body);

    CommentDto update(long id, String body);

    CommentDto findById(long id);

    void delete(long id);

}
