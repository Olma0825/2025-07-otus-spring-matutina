package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.BookDetailsDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Override
    public BookDto findById(String id) {

        return BookDto.toDto(bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id=%s not found".formatted(id))));
    }

    @Override
    public BookDetailsDto findBookByIdWithComments(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id=%s not found".formatted(id)));
        List<Comment> comments = commentRepository.findByBookId(id);
        return BookDetailsDto.toDto(book, comments);
    }

    @Override
    public List<BookDto> findAll() {
        List<Book> books = bookRepository.findAll();

        Set<String> authorIds = books.stream()
                .map(book -> book.getAuthor().getId())
                .collect(Collectors.toSet());
        Set<String> genreIds = books.stream()
                .map(book -> book.getGenre().getId())
                .collect(Collectors.toSet());

        Map<String, Author> authors = authorRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(Author::getId, a -> a));
        Map<String, Genre> genres = genreRepository.findAllById(genreIds).stream()
                .collect(Collectors.toMap(Genre::getId, g -> g));

        for (Book book : books) {
            Author fullAuthor = authors.get(book.getAuthor().getId());
            Genre fullGenre = genres.get(book.getGenre().getId());
            book.setAuthor(fullAuthor);
            book.setGenre(fullGenre);
        }
        return books.stream().map(BookDto::toDto).toList();
    }

    @Override
    public BookDto insert(String title, String authorId, String genreId) {
        return BookDto.toDto(save(null, title, authorId, genreId));
    }

    @Override
    public BookDto update(String id, String title, String authorId, String genreId) {
        return BookDto.toDto(save(id, title, authorId, genreId));
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteAllByBookId(id);
        bookRepository.deleteById(id);
    }

    private Book save(String id, String title, String authorId, String genreId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %s not found".formatted(genreId)));
        var book = new Book(id, title, author, genre);
        return bookRepository.save(book);
    }
}
