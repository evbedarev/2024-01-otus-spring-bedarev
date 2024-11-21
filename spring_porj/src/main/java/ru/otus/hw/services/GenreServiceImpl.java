package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityAlreadyExistsException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.RelatedEntityException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private static final Logger logger = LoggerFactory.getLogger(GenreServiceImpl.class);

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre findById(long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id=%d not found".formatted(id)));
        return genre;
    }

    @Override
    public Genre insert(String name) {
        if (genreRepository.findByName(name).isPresent()) {
            throw new EntityAlreadyExistsException("Genre with name %s already exists".formatted(name));
        }
        logger.info("Insert new genre name: %s".formatted(name));
        return genreRepository.save(new Genre(0, name));
    }

    @Override
    public void delete(long id){
        if (bookRepository.findFirstByGenreId(id).isPresent()) {
            throw new RelatedEntityException("Genre with id %s linked with books.".formatted(id) +
                    "remove links, then delete genre");
        }
        logger.info("Delete genre id: %s".formatted(id));
        genreRepository.deleteById(id);
    }
}
