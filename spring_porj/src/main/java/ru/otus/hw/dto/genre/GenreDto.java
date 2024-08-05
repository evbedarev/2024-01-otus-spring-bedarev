package ru.otus.hw.dto.genre;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.hw.models.Genre;

@NoArgsConstructor
public class GenreDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    @Size(min = 3, message = "Minimum length 3 chars")
    private String name;

    @Getter
    @Setter
    private boolean hasError = false;

    @Getter
    @Setter
    private String errText;

    public GenreDto (long id, String name) {
        this.id = id;
        this.name = name;
    }

    public GenreDto (long id, String name, String errText) {
        this.id = id;
        this.name = name;
        this.hasError = true;
        this.errText = errText;
    }

    public static GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
