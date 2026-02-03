package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find comments for current book by books id", key = "acb")
    String findByBookId(long bookId) {
        return commentService.findByBookId(bookId).stream().map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find comment by id", key = "cbid")
    String findById(long id) {
        return commentService.findById(id).map(commentConverter::commentToString)
                .orElse("Comment with id = %s not found ".formatted(id));
    }

    @ShellMethod(value = "insert comment", key = "cins")
    String insertComment(long bookId, String body) {
        var savedComment = commentService.insert(bookId, body);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "update comment", key = "cupd")
    String updateComment(long id, String body) {
        var updatedComment = commentService.update(id, body);
        return commentConverter.commentToString(updatedComment);
    }

    @ShellMethod(value = "delete comment", key = "cdel")
    void delete(long id) {
        commentService.delete(id);
    }

}
