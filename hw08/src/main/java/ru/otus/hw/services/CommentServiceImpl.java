package ru.otus.hw.services;

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
    public List<CommentDto> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId).stream().map(CommentDto::toDto).toList();
    }

    @Override
    public CommentDto findById(String id) {
        return CommentDto.toDto(commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id=%s not found".formatted(id))));
    }

    @Override
    public CommentDto insert(String bookId, String body) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id = %s not found".formatted(bookId)));
        Comment comment = new Comment(body, book);
        return CommentDto.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto update(String id, String body) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = %s not found ".formatted(id)));
        existingComment.setBody(body);
        return CommentDto.toDto(commentRepository.save(existingComment));
    }

    @Override
    public void delete(String id) {
        commentRepository.deleteById(id);
    }
}
