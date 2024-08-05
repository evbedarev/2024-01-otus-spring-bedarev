package ru.otus.hw.rest;


import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.author.AuthorDto;
import ru.otus.hw.dto.author.AuthorErrorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.RelatedEntityException;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthorRestController {

    private final AuthorService authorService;

    public AuthorRestController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping(value = "/api/v1/authors/find")
    public AuthorDto findAuthorByFullName(@RequestBody AuthorDto authorDto) {
        return AuthorDto.toDto(authorService.findByFullName(authorDto.getFullName()));
    }

    @GetMapping("/api/v1/authors")
    public List<AuthorDto> listAllBooks() {
        return authorService.findAll().stream()
                .map(AuthorDto::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/api/v1/authors")
    public AuthorDto createNewAuthor(@Valid @RequestBody AuthorDto authorDto, BindingResult bindingResult) {
        Author author;
        if (bindingResult.hasErrors()) {
            return createErrDto(bindingResult.getFieldError("fullName").getDefaultMessage());
        }
        try {
            author = authorService.insert(authorDto.getFullName(), authorDto.getAboutAuthor());
        } catch (EntityNotFoundException ex) {
            return createErrDto(ex.getMessage());
        }
        return AuthorDto.toDto(author);
    }

    @PatchMapping("/api/v1/authors")
    public AuthorDto updateAuthor(@Valid @RequestBody AuthorDto authorDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return createErrDto(bindingResult.getFieldError("fullName").getDefaultMessage());
        }
        try {
            Author author = authorService.update(authorDto.getId(), authorDto.getFullName(),authorDto.getAboutAuthor());
            return AuthorDto.toDto(author);
        } catch (EntityNotFoundException exception) {
            return createErrDto(exception.getMessage());
        }
    }

    @DeleteMapping("/api/v1/authors/{id}")
    public AuthorDto deleteAuthorById(@PathVariable("id") long id) {
        try {
            authorService.delete(id);
        } catch (RelatedEntityException exception) {
            return createErrDto(exception.getMessage());
        }
        return new AuthorDto();
    }

    public AuthorDto createErrDto(String error) {
        AuthorErrorDto authorErrorDto = new AuthorErrorDto();
        authorErrorDto.setHasErrors(true);
        authorErrorDto.setFullNameError(error);
        return new AuthorDto(authorErrorDto);
    }
}
