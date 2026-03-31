package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream().map(AuthorDto::toDto).toList();
    }

    @Override
    public AuthorDto findById(String id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id=%s not found".formatted(id)));
        return AuthorDto.toDto(author);
    }

    @Override
    public AuthorDto insert(String fullName) {
        return save(null, fullName);
    }

    private AuthorDto save(String id, String fullName) {
        Author author = new Author(id, fullName);
        return AuthorDto.toDto(authorRepository.save(author));
    }

    @Override
    public AuthorDto update(String id, String fullName) {
        return save(id, fullName);
    }

    @Override
    public void delete(String id) {

        if (bookRepository.existsByAuthorId(id)) {
            throw new EntityNotFoundException(("You can't delete an author from id = %s because there are books " +
                    "of this author.")
                    .formatted(id));
        }

        authorRepository.deleteById(id);
    }
}
