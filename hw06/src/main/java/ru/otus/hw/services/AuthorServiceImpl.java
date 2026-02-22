package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorDto> findAll() {

        return authorRepository.findAll().stream().map(AuthorDto::toDto).toList();
    }

    @Override
    public AuthorDto findById(long id) {

        Author authorDto = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id=%d not found".formatted(id)));
        return AuthorDto.toDto(authorDto);
    }

    @Transactional
    @Override
    public AuthorDto insert(String fullName) {

        return save(0, fullName);
    }

    private AuthorDto save(long id, String fullName) {
        Author author = new Author(id, fullName);
        return AuthorDto.toDto(authorRepository.save(author));
    }

    @Transactional
    @Override
    public AuthorDto update(long id, String fullName) {
        return save(id, fullName);
    }
}
