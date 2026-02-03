package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    List<Comment> findByBookId(long bookId);

    Comment insert(long bookId, String body);

    Comment update(long id, String body);

    Optional<Comment> findById(long id);

    void delete(long id);

}
