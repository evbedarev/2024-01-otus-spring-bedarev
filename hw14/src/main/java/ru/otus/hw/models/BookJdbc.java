package ru.otus.hw.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class BookJdbc {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String authorId;

    @Getter
    @Setter
    private String genreId;

    @Override
    public String toString() {
        return "Book id: %s, title: %s, author_id: %s, genre_id: %s"
                .formatted(getId(), getTitle(), getAuthorId(), getGenreId());
    }
}
