package ru.otus.hw.dto;

import ru.otus.hw.models.Book;

public class BookModifyDto {

    private String id;

    private String title;

    private String authorId;

    private String genreId;

    public BookModifyDto() {
    }

    public BookModifyDto(String id, String title, String authorId) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
    }

    public BookModifyDto(String title, String authorId) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public static BookModifyDto toDto(Book book) {
        return new BookModifyDto(book.getId(),
                book.getTitle(),
                book.getAuthor().getId());
    }
}
