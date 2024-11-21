package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.otus.hw.models.Book;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест репозитория книг")
@DataJpaTest
public class BookRepositoryTest {

    private static final int PAGE_SIZE = 5;

    private static final String SORTED_BY = "id";

    private static final List<String> TITLE_LIST_EXPECT = Arrays.asList("Война и Мир",
            "Капитанская дочка", "Пиковая дама", "Метель", "Гроза");

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void shouldCorrectReturnBookById() {
        Optional<Book> book = bookRepository.findById(2L);
        assertThat(book.get().getTitle()).isEqualTo("Капитанская дочка");
        assertThat(book.get().getAuthor().getFullName()).isEqualTo("Александр Сергеевич Пушкин");
    }

    @Test
    public void shouldCorrectReturnPageOfBooks() {
        Pageable firstFiveElm = PageRequest.of(0, PAGE_SIZE, Sort.by(SORTED_BY));
        Page<Book> books = bookRepository.findAll(firstFiveElm);
        IntStream.range(0,TITLE_LIST_EXPECT.size() - 1).boxed()
                        .forEach(i -> assertThat(books.get())
                                .anyMatch( s -> s.getTitle().equals(TITLE_LIST_EXPECT.get(i))));
        assertThat(books.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void shouldCorrectFindBookByTitle() {
        Optional<Book> book = bookRepository.findByTitle(TITLE_LIST_EXPECT.get(1));
        assertThat(book.get().getId()).isEqualTo(2);
    }

    @Test
    public void shouldCorrectFindBooksByAuthor() {
        List<Book> books = bookRepository.findByAuthorFullName("Александр Николаевич Островский");
        assertThat(books).anyMatch(b -> b.getTitle().equals("Гроза"));
        assertThat(books).anyMatch(b -> b.getTitle().equals("Бесприданница"));
    }

    @Test
    public void shouldCorrectFindFirstBookByAuthorId() {
        Optional<Book> optionalBook = bookRepository.findFirstByAuthorId(1L);
        assertThat(optionalBook).isPresent();
    }

    @Test
    public void shouldCorrectFindFirstBookByGenreId() {
        Optional<Book> optionalBook = bookRepository.findFirstByGenreId(1L);
        assertThat(optionalBook).isPresent();
    }

}
