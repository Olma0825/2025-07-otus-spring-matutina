package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(GenreDto::toDto).toList();
    }

    @Override
    public GenreDto findById(String id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id=%s not found".formatted(id)));
        return GenreDto.toDto(genre);
    }

    private GenreDto save(String id, String name) {

        return GenreDto.toDto(genreRepository.save(new Genre(id, name)));
    }

    @Override
    public GenreDto insert(String name) {

        return save(null, name);
    }

    @Override
    public GenreDto update(String id, String name) {

        return save(id, name);
    }

    @Override
    public void delete(String id) {
        if (bookRepository.existsByGenreId(id)) {
            throw new EntityNotFoundException(("You can't delete a genre from id = %s because there are books " +
                    "of this genre.")
                    .formatted(id));
        }

        genreRepository.deleteById(id);
    }

}
