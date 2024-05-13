package ru.otus.hw.rest;


import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.otus.hw.dto.ErrorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookModifyDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BookRestController {
    private final BookService bookService;

    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/api/v1/books")
    public List<BookDto> listAllBooks() {
        return bookService.findAll().stream()
                .map(BookDto::toDt0)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v1/books/{id}")
    public BookDto getBookById(@PathVariable("id") long id) {
        Book book = bookService.findById(id).orElseThrow(EntityNotFoundException::new);
        return BookDto.toDt0(book);
    }

    @PostMapping("/api/v1/books")
    public BookDto createNewBook(@RequestBody BookModifyDto bookModifyDto) {
        Book book = bookService.insert(bookModifyDto.getTitle(),
                bookModifyDto.getAuthorId(),
                bookModifyDto.getGenreId());
        return BookDto.toDt0(book);
    }

    @PatchMapping("/api/v1/books")
    public BookDto updateBookById(@Valid @RequestBody BookModifyDto bookModifyDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return createErrorDto(bindingResult);
        }
        try {
            Book book = bookService.update(bookModifyDto.getId(),
                    bookModifyDto.getTitle(),
                    bookModifyDto.getAuthorId(),
                    bookModifyDto.getGenreId());
            return BookDto.toDt0(book);
        } catch (EntityNotFoundException ex) {
            checkExceptionAndAddFieldErr(ex, bindingResult);
            return createErrorDto(bindingResult);
        }
    }

    @DeleteMapping("/api/v1/books/{id}")
    public void deleteBookById(@PathVariable("id") long id) {
        bookService.deleteById(id);
    }

    private void checkExceptionAndAddFieldErr(EntityNotFoundException ex, BindingResult bindingResult) {
        if (ex.getMessage().contains("Author")) {
            bindingResult.addError(new FieldError("book", "authorId", "Author with id not found"));
        }
        if (ex.getMessage().contains("Genre")) {
            bindingResult.addError(new FieldError("book", "genreId", "Genre with id not found"));
        }
    }

    private List<String> getAllErrorsFromResult(BindingResult bindingResult) {
        return bindingResult
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }

    private BookDto createErrorDto(BindingResult bindingResult) {
        ErrorDto errorConverter = new ErrorDto(true);
        errorConverter.convertStringsToErr(getAllErrorsFromResult(bindingResult));
        return new BookDto(errorConverter);
    }
}
