package ru.otus.hw.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Book;
import ru.otus.hw.repository.AuthorRepository;
import ru.otus.hw.repository.GenreRepository;
import ru.otus.hw.repository.BookRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    private final List<Author> authors = Arrays.asList(
            new Author("Author_1"),
            new Author("Author_2"),
            new Author("Author_3"),
            new Author("Author_4"),
            new Author("Author_5"),
            new Author("Author_6"),
            new Author("Author_7"),
            new Author("Author_8"),
            new Author("Author_9")
    );

    private final List<Genre> genres = Arrays.asList(
            new Genre("Genre_1"),
            new Genre("Genre_2"),
            new Genre("Genre_3"),
            new Genre("Genre_4"),
            new Genre("Genre_5"),
            new Genre("Genre_6"),
            new Genre("Genre_7"),
            new Genre("Genre_8"),
            new Genre("Genre_9"),
            new Genre("Genre_10")
    );

    private List<Book> books = new ArrayList<>();

    @ChangeSet(order = "001", id = "insertAuthors", author = "bev")
    public void insertAuthors(AuthorRepository repository) {
        authors.forEach(repository::save);
    }

    @ChangeSet(order = "002", id = "insertGenres", author = "bev")
    public void insertGenres(GenreRepository repository) {
        genres.forEach(repository::save);
    }

    @ChangeSet(order = "003", id = "insertBooks", author = "bev")
    public void insertBooks(BookRepository repository) {
        books.add(new Book("Title_1", authors.get(0), genres.get(0)));
        books.add(new Book("Title_2", authors.get(0), genres.get(1)));
        books.add(new Book("Title_4", authors.get(2), genres.get(1)));
        books.add(new Book("Title_5", authors.get(2), genres.get(3)));
        books.add(new Book("Title_6", authors.get(3), genres.get(4)));
        books.add(new Book("Title_7", authors.get(4), genres.get(5)));
        books.add(new Book("Title_8", authors.get(5), genres.get(6)));
        books.add(new Book("Title_9", authors.get(6), genres.get(7)));
        books.forEach(repository::save);
    }
}
