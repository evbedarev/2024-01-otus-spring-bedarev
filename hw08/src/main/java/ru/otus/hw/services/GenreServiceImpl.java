package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repository.GenreRepository;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre findById(String id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id=%s not found".formatted(id)));
        return genre;
    }
}
