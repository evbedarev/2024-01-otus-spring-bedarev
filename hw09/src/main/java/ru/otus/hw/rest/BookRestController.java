package ru.otus.hw.rest;


import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/api/books")
    public List<BookDto> listAllBooks() {
         return  bookService.findAll().stream()
                .map(BookDto::toDt0)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/books/{id}")
    public BookDto getBookById(@PathVariable("id") long id) {
        Book book = bookService.findById(id).orElseThrow(EntityNotFoundException::new);
        return BookDto.toDt0(book);
    }

    @PostMapping("/api/books")
    public BookDto createNewBook(@RequestBody BookModifyDto bookModifyDto) {
        Book book = bookService.insert(bookModifyDto.getTitle(),
                bookModifyDto.getAuthorId(),
                bookModifyDto.getGenreId());
        return BookDto.toDt0(book);
    }

    @PatchMapping("/api/books")
    public BookDto updateBookById(@RequestBody BookModifyDto bookModifyDto) {
        Book book = bookService.update(bookModifyDto.getId(),
                bookModifyDto.getTitle(),
                bookModifyDto.getAuthorId(),
                bookModifyDto.getGenreId());
        return BookDto.toDt0(book);
    }

    @DeleteMapping("/api/books/{id}")
    public void deleteBookById(@PathVariable("id") long id) {
        bookService.deleteById(id);
    }
}
