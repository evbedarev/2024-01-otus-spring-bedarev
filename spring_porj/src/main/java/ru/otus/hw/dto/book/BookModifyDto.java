package ru.otus.hw.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.otus.hw.models.Book;

public class BookModifyDto {
    private long id;

    @Pattern.List({
            @Pattern(regexp = "^(?!title$)(.+)", message = "Title wrong. Change default value")
    })
    @Size(min = 2, message = "Title input more then 2 chars")
    private String title;

    @Min(value = 1, message = "Author id=0, not valid id")
    private long authorId;

    @Min(value = 1, message = "Genre id=0, not valid id ")
    private long genreId;

    public BookModifyDto() {
    }

    public BookModifyDto(long id, String title, long authorId, long genreId) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.genreId = genreId;
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

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public long getGenreId() {
        return genreId;
    }

    public void setGenreId(long genreId) {
        this.genreId = genreId;
    }

    public static BookModifyDto toDto(Book book) {
        return new BookModifyDto(book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getGenre().getId());
    }
}
