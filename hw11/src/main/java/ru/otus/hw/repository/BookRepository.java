package ru.otus.hw.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Book;

public interface BookRepository extends ReactiveMongoRepository<Book, String> {
    Flux<Book> findAll();
}
