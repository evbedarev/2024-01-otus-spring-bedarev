package ru.otus.hw.rest;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookModifyDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.repository.AuthorRepository;
import ru.otus.hw.repository.BookRepository;


@RestController
public class BookRestController {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    public BookRestController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @GetMapping("/api/v1/books")
    public Flux<BookDto> listAllBooks() {
        return bookRepository.findAll().map(BookDto::toDt0);
    }

    @GetMapping("/api/v1/books/{id}")
    public Mono<BookDto> getBookById(@PathVariable("id") String id) {
        return bookRepository.findById(id).map(BookDto::toDt0);
    }

    @PostMapping("/api/v1/books")
    public Mono<ResponseEntity<BookDto>> createNewBook(@RequestBody BookModifyDto bookModifyDto) {
        return
                authorRepository.findById(bookModifyDto.getAuthorId())
                .map(author -> new Book(bookModifyDto.getTitle(),author))
                .flatMap(bookRepository::save)
                .map(BookDto::toDt0)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PatchMapping("/api/v1/books")
    public Mono<ResponseEntity<BookDto>> updateBookById(@RequestBody BookModifyDto bookModifyDto) {
        return bookRepository.findById(bookModifyDto.getId())
                .flatMap(book -> authorRepository.findById(bookModifyDto.getAuthorId())
                        .map(author -> new Book(book.getId(), bookModifyDto.getTitle(), author))
                )
                .flatMap(bookRepository::save)
                .map(BookDto::toDt0)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/api/v1/books/{id}")
    public Mono<Void> deleteBookById(@PathVariable("id") String id) {
        return bookRepository.deleteById(id);
    }
}
