package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDetailsDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    private final CommentConverter commentConverter;

    public String bookToString(BookDto book) {
        return "Id: %d, title: %s, author: {%s}, genres: [%s]".formatted(
                book.id(),
                book.title(),
                authorConverter.authorToString(book.author()),
                genreConverter.genreToString(book.genre()));
    }

    public String bookWithCommentsToString(BookDetailsDto book) {
        List<CommentDto> commentList = book.comments();
        String commentsString = commentList.stream().map(commentConverter::commentToString).map(s -> "\t" + s)
                .collect(Collectors.joining("," + System.lineSeparator()));
        return "Id: %d, title: %s, author: {%s}, genres: [%s], comments: [%s]".formatted(
                book.id(),
                book.title(),
                authorConverter.authorToString(book.author()),
                genreConverter.genreToString(book.genre()),
                commentsString);
    }
}
