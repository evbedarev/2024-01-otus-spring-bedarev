package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.hw.models.Author;

public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {
}
