package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Genre;


public interface GenreRepository extends MongoRepository<Genre, String> {
}
