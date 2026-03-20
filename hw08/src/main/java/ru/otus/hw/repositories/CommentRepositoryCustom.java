package ru.otus.hw.repositories;

import java.util.List;

public interface CommentRepositoryCustom {
    void deleteAllByBookIds(List<String> bookIds);
}
