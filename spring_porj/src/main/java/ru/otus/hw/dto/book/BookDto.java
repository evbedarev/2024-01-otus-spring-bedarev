package ru.otus.hw.dto.book;

import lombok.Getter;
import lombok.Setter;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

public class BookDto {
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private Author author;

    @Getter
    @Setter
    private Genre genre;

    @Getter
    @Setter
    private BookErrorDto err;

    public BookDto() {
    }

    public BookDto(long id, String title, Author author, Genre genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public BookDto(BookErrorDto err) {
        this.err = err;
    }
    public static Book toDomainObject(BookDto dto) {
        return new Book(dto.getId(), dto.getTitle(), dto.getAuthor(), dto.getGenre());
    }

    public static BookDto toDt0(Book book) {
        return new BookDto(book.getId(),book.getTitle(),book.getAuthor(),book.getGenre());
    }

}
