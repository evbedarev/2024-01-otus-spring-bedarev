package ru.otus.hw.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void deleteAll() {
        bookRepository.deleteAll().block();
    }

    @Test
    void shouldCorrectAddNewBook() {
        Mono<Author> authorMono = authorRepository.save(new Author("new_author"));
        Mono<Book> bookMono = bookRepository.save(new Book("title", authorMono.block()));
        StepVerifier
                .create(bookMono)
                .assertNext(book -> {
                    assertNotNull(book.getId());
                    assertEquals(book.getTitle(), "title");
                })
                .expectComplete()
                .verify();
    }

    @Test
    void shouldCorrectUpdateBook() {
        Mono<Author> authorMono = authorRepository.save(new Author("new_author"));
        Author author = authorMono.block();
        Mono<Book> bookMono = bookRepository.save(new Book("title", author));
        String id = bookMono.block().getId();
        Mono<Book> bookMonoChanged = bookRepository.save(new Book(id, "new_title", author));
        StepVerifier
                .create(bookMonoChanged)
                .assertNext(book -> assertEquals(book.getId(), id))
                .expectComplete()
                .verify();
        StepVerifier
                .create(bookMonoChanged)
                .assertNext(book -> assertEquals(book.getTitle(), "new_title"))
                .expectComplete()
                .verify();
    }


    @Test
    void shouldReturnCorrectBookById() {
        Mono<Author> authorMono = authorRepository.save(new Author("new_author"));
        Author author = authorMono.block();
        Mono<Book> bookMono = bookRepository.save(new Book("title_get_by_id", author));
        String id = bookMono.block().getId();
        Mono<Book> actualBook = bookRepository.findById(id);
        System.out.println(actualBook.block().getId());
        StepVerifier
                .create(actualBook)
                .assertNext(book -> {
                    assertEquals(book.getTitle(), "title_get_by_id");
                    assertEquals(book.getId(), id);
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldReturnCorrectBookList() {
        Mono<Author> authorMono = authorRepository.save(new Author("new_author"));
        Author author = authorMono.block();
        Mono<Book> bookMonoOne = bookRepository.save(new Book("title_one", author));
        String idOne = bookMonoOne.block().getId();
        Mono<Book> bookMonoTwo = bookRepository.save(new Book("title_two", author));
        String idTwo = bookMonoTwo.block().getId();
        Flux<Book> actualBooks = bookRepository.findAll();
        StepVerifier
                .create(actualBooks)
                .assertNext(book -> assertEquals(book.getId(), idOne))
                .assertNext(book -> assertEquals(book.getId(), idTwo))
                .expectComplete()
                .verify();
    }
}

