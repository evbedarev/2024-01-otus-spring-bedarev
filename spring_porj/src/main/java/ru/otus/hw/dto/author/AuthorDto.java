package ru.otus.hw.dto.author;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.otus.hw.models.Author;

@AllArgsConstructor
public class AuthorDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    @Size(min = 5, message = "Full name input more then 5 chars")
    private String fullName;

    @Getter
    @Setter
    private String aboutAuthor;

    @Getter
    @Setter
    private AuthorErrorDto authorErrorDto = new AuthorErrorDto(false);

    public AuthorDto () {
    }

    public AuthorDto(AuthorErrorDto authorErrorDto) {
        this.authorErrorDto = authorErrorDto;
    }

    public AuthorDto(long id, String fullName, String aboutAuthor) {
        this.id = id;
        this.fullName = fullName;
        this.aboutAuthor = aboutAuthor;
    }

    public static Author toDomainObject(AuthorDto authorDto) {
        return new Author(authorDto.getId(), authorDto.getFullName(), authorDto.getAboutAuthor());
    }
    public static AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName(), author.getAboutAuthor(),
                new AuthorErrorDto(false));
    }
}
