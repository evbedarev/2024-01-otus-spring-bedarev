package ru.otus.hw.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ErrorDto {

    @Setter
    @Getter
    private boolean hasErrors;

    @Getter
    @Setter
    private String authorError;

    @Getter
    @Setter
    private String genreError;

    @Getter
    @Setter
    private String titleError;


    public ErrorDto(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public void convertStringsToErr(List<String> listErr) {
        for (String err: listErr) {
            if (err.contains("Author")) {
                setAuthorError(err);
            }
            if (err.contains("Genre")) {
                setGenreError(err);
            }
            if (err.contains("Title")) {
                setTitleError(err);
            }
        }
    }
}
