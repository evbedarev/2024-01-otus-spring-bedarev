package ru.otus.hw.rest;


import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
import ru.otus.hw.dto.author.AuthorDto;
import ru.otus.hw.dto.book.*;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookTextService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class BookRestController {
    private final BookService bookService;

    private final BookTextService bookTextService;

    public BookRestController(BookService bookService, BookTextService bookTextService) {
        this.bookService = bookService;
        this.bookTextService = bookTextService;
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

    @GetMapping("/api/v1/books/pages/{page}")
    public BookPagesDto getBookPage(@PathVariable("page") int page) {
        Page<Book> bookPage = bookService.findAllInPages(page);
        return new BookPagesDto(bookPage.stream().collect(Collectors.toList()), bookPage.getTotalPages());
    }

    @PostMapping("/api/v1/books/find/title")
    public BookDto getBookByTitle(@RequestBody BookModifyDto bookModifyDto) {
        Optional<Book> bookFromDb = bookService.findByTitle(bookModifyDto.getTitle());
        if (bookFromDb.isPresent()) {
            return BookDto.toDt0(bookFromDb.get());
        } else {
            BookErrorDto bookErrorDto = new BookErrorDto(true);
            bookErrorDto.setTitleError("Title '%s' not found".formatted(bookModifyDto.getTitle()));
            return new BookDto(bookErrorDto);
        }
    }

    @PostMapping("/api/v1/books/find/author")
    public List<BookDto> getBookByAuthor(@RequestBody AuthorDto authorDto) {
        return bookService.findByAuthorFullName(authorDto.getFullName()).stream()
                .map(BookDto::toDt0).collect(Collectors.toList());
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

    @PostMapping("/api/v1/books/text")
    public String addTextForBook(@RequestBody TextBookDto textBookDto) {
        System.out.println("part num %s".formatted(textBookDto.getPartNumber()));
        bookTextService.insertText(textBookDto.getUnformatedText(),
                textBookDto.getPartNumber(),
                textBookDto.getBookId());
        return "ok";
    }

    @GetMapping("/api/v1/books/text/{id}")
    public TextBookDto getBookText(@PathVariable("id") long id) {
        return bookTextService.findByBookId(id);
    }

    @GetMapping("/api/v1/books/text/{bookid}/{part}")
    public TextBookDto getBookTextByBookIdAndPartNum(@PathVariable("bookid") long bookid,
                                                     @PathVariable("part") int part) {
        return bookTextService.findByBookIdAndPartNumber(bookid, part);
    }

    private List<String> getAllErrorsFromResult(BindingResult bindingResult) {
        return bindingResult
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }

    private BookDto createErrorDto(BindingResult bindingResult) {
        BookErrorDto errorConverter = new BookErrorDto(true);
        errorConverter.convertStringsToErr(getAllErrorsFromResult(bindingResult));
        return new BookDto(errorConverter);
    }
}
