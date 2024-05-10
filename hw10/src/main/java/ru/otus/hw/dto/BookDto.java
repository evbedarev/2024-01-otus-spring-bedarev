package ru.otus.hw.dto;

import ru.otus.hw.converter.ErrorConverter;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

public class BookDto {
    private long id;

    private String title;

    private Author author;

    private Genre genre;

    private ErrorConverter err;

    public BookDto() {
    }

    public BookDto(long id, String title, Author author, Genre genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public BookDto(ErrorConverter err) {
        this.err = err;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public static Book toDomainObject(BookDto dto) {
        return new Book(dto.getId(), dto.getTitle(), dto.getAuthor(), dto.getGenre());
    }

    public static BookDto toDt0(Book book) {
        return new BookDto(book.getId(),book.getTitle(),book.getAuthor(),book.getGenre());
    }

    public ErrorConverter getErr() {
        return err;
    }

    public void setErr(ErrorConverter err) {
        this.err = err;
    }
}
