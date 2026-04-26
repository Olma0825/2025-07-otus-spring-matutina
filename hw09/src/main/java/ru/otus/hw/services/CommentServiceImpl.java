package ru.otus.hw.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    public List<CommentDto> findByBookId(long bookId) {
        return commentRepository.findByBookId(bookId).stream().map(CommentDto::toDto).toList();
    }

    @Override
    public CommentDto findById(long id) {
        return CommentDto.toDto(commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id=%d not found".formatted(id))));
    }

    @Transactional
    @Override
    public CommentDto insert(long bookId, String body) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id = %d not found".formatted(bookId)));
        Comment comment = new Comment(body, book);
        return CommentDto.toDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(long id, String body) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = %d not found ".formatted(id)));
        existingComment.setBody(body);
        return CommentDto.toDto(commentRepository.save(existingComment));
    }

    @Override
    @Transactional
    public void delete(long id) {
        commentRepository.deleteById(id);
    }
}
