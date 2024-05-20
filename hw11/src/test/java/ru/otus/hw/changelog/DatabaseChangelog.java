package ru.otus.hw.changelog;

import io.mongock.api.annotations.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repository.AuthorRepository;
import ru.otus.hw.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

@ChangeUnit(id="homework8", order = "1", author = "bev")
public class DatabaseChangelog {

    private List<Author> authors = new ArrayList<>();

    private List<Book> books = new ArrayList<>();

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    private final MongoTemplate mongoTemplate;

    public DatabaseChangelog(AuthorRepository authorRepository, BookRepository bookRepository, MongoTemplate mongoTemplate) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.mongoTemplate = mongoTemplate;
    }


    @BeforeExecution
    public void beforeExecution() {
        clearCollections();
    }

    @Execution
    public void initDb(AuthorRepository repository, BookRepository bookRepository) {
        Author authorOne = repository.save(new Author("Author_1")).block();
        repository.save(new Author("Author_2")).block();
        repository.save(new Author("Author_3")).block();
        bookRepository.save(new Book("Title_1", authorOne)).block();

    }

    private void clearCollections() {
        authorRepository.deleteAll().block();
        bookRepository.deleteAll().block();
    }

    @RollbackExecution
    public void rollback() {
        clearCollections();
    }

    @RollbackBeforeExecution
    public void rollbackBefore() {
        clearCollections();
    }
}
