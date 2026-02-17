package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDetailsDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(long id) {

        return BookDto.toDto(bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id=%d not found".formatted(id))));
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailsDto findBookWithComments(long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id=%d not found".formatted(id)));
        List<Comment> comments = commentRepository.findByBookId(id);
        return BookDetailsDto.toDto(book, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(BookDto::toDto).toList();
    }

    @Override
    @Transactional
    public BookDto insert(String title, long authorId, long genreId) {
        return BookDto.toDto(save(0, title, authorId, genreId));
    }

    @Override
    @Transactional
    public BookDto update(long id, String title, long authorId, long genreId) {
        return BookDto.toDto(save(id, title, authorId, genreId));
    }

    @Override
    @Transactional
    public void deleteById(long id) {

        List<Comment> comments = commentRepository.findByBookId(id);
        for (Comment comment: comments) {
            commentRepository.deleteById(comment.getId());
        }
        bookRepository.deleteById(id);
    }

    private Book save(long id, String title, long authorId, long genreId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(genreId)));
        var book = new Book(id, title, author, genre);
        return bookRepository.save(book);
    }
}
