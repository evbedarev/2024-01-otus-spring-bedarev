package ru.otus.hw.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repository.AuthorRepository;
import ru.otus.hw.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    private List<Author> authors = new ArrayList<>();

    private List<Book> books = new ArrayList<>();

    @ChangeSet(order = "001", id = "drop DB", author = "bev", runAlways = true)
    public void dropDatabase(MongoDatabase mongoDatabase) {
        mongoDatabase.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "bev")
    public void insertAuthors(AuthorRepository repository) {
        authors.add(repository.save(new Author("Author_1")).block());
        authors.add(repository.save(new Author("Author_2")).block());
        authors.add(repository.save(new Author("Author_3")).block());
    }

    @ChangeSet(order = "003", id = "insertBooks", author = "bev")
    public void insertBooks(BookRepository repository) {
        books.add(new Book("Title_1", authors.get(0)));
        books.add(new Book("Title_2", authors.get(1)));
        books.add(new Book("Title_3", authors.get(2)));
        books.forEach(repository::save);
    }
}
