package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(GenreDto::toDto).toList();
    }

    @Override
    public GenreDto findById(long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id=%d not found".formatted(id)));
        return GenreDto.toDto(genre);
    }

    private GenreDto save(long id, String name) {

        return GenreDto.toDto(genreRepository.save(new Genre(id, name)));
    }

    @Override
    @Transactional
    public GenreDto insert(String name) {

        return save(0, name);
    }

    @Override
    @Transactional
    public GenreDto update(long id, String name) {

        return save(id, name);
    }

}
