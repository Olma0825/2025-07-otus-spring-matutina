package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> findByBookId(String bookId);

    CommentDto insert(String bookId, String body);

    CommentDto update(String id, String body);

    CommentDto findById(String id);

    void delete(String id);

}
