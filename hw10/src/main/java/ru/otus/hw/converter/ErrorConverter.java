package ru.otus.hw.converter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ErrorConverter {

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


    public ErrorConverter(boolean hasErrors) {
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
