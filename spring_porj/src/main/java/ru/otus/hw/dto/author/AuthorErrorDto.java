package ru.otus.hw.dto.author;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class AuthorErrorDto {

    @Setter
    @Getter
    private boolean hasErrors = false;

    @Getter
    @Setter
    private String fullNameError;

    public AuthorErrorDto(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }
}
