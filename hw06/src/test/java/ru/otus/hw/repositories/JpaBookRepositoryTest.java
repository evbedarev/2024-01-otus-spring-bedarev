package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе jpa для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class})
class JpaBookRepositoryTest {

    private final static int EXPECTED_BOOKS_COUNT = 3;

    private final static int FIRST_BOOK_ID = 1;

    private final static int NEW_ID_FOR_INSERT_BOOK = 4;

    private final static String NEW_BOOK_TITLE = "new title";

    @Autowired
    private JpaBookRepository jpaBookRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {
        Optional<Book> actualBook = jpaBookRepository.findById(expectedBook.getId());
        assertThat(actualBook).isPresent().get()
                .matches(b -> b.getGenre().getName().equals(expectedBook.getGenre().getName()))
                .matches(b -> b.getAuthor().getFullName().equals(expectedBook.getAuthor().getFullName()));
    }

    @DisplayName("должен загружать список всех книг за 1 запрос")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> books = jpaBookRepository.findAll();
        assertThat(books).isNotNull().hasSize(EXPECTED_BOOKS_COUNT);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        Author author = getAuthor(NEW_ID_FOR_INSERT_BOOK - 1);
        Genre genre = getGenre(NEW_ID_FOR_INSERT_BOOK - 1);
        Book expectedBook = new Book(NEW_ID_FOR_INSERT_BOOK, NEW_BOOK_TITLE, author, genre);
        jpaBookRepository.save(expectedBook);
        Book actualBook = em.find(Book.class, NEW_ID_FOR_INSERT_BOOK);
        assertThat(actualBook)
                .matches(s -> s.getTitle().equals(NEW_BOOK_TITLE))
                .matches(s -> s.getAuthor().getFullName().equals(author.getFullName()))
                .matches(s -> s.getGenre().getName().equals(genre.getName()));
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        Author author = getAuthor(FIRST_BOOK_ID + 1);
        Genre genre = getGenre(FIRST_BOOK_ID + 1);
        Book expectedBook = new Book(FIRST_BOOK_ID, NEW_BOOK_TITLE, author, genre);
        jpaBookRepository.save(expectedBook);
        Book actualBook = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(actualBook)
                .matches(s -> s.getTitle().equals(NEW_BOOK_TITLE))
                .matches(s -> s.getAuthor().getFullName().equals(author.getFullName()))
                .matches(s -> s.getGenre().getName().equals(genre.getName()));

    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        jpaBookRepository.deleteById(FIRST_BOOK_ID);
        Optional<Book> deletedBook = Optional.ofNullable(em.find(Book.class, FIRST_BOOK_ID));
        assertThat(deletedBook).isNotPresent();
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(JpaBookRepositoryTest::getAuthor)
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 4).boxed()
                .map(JpaBookRepositoryTest::getGenre)
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id, "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.get(id - 1)))
                .toList();
    }
    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }

    private static Author getAuthor(int id) {
        return new Author(id, "Author_" + id);
    }


    private static Genre getGenre(int id) {
        return new Genre(id, "Genre_" + id);
    }
}