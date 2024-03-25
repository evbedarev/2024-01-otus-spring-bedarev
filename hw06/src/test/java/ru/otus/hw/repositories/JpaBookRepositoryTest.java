package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе jpa для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class})
class JpaBookRepositoryTest {

    private final static long EXPECTED_COUNT_QUERIES = 1L;
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
        SessionFactory sessionFactory = em.getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        List<Book> books = jpaBookRepository.findAll();
        assertThat(books).isNotNull().hasSize(EXPECTED_BOOKS_COUNT)
                .containsAll(getDbBooks());
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_COUNT_QUERIES);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        Book expectedBook = new Book(
                NEW_ID_FOR_INSERT_BOOK,
                NEW_BOOK_TITLE,
                getAuthor(NEW_ID_FOR_INSERT_BOOK),
                getGenre(NEW_ID_FOR_INSERT_BOOK),
                getSingleComment(NEW_ID_FOR_INSERT_BOOK));
        jpaBookRepository.save(expectedBook);
        Optional<Book> actualBook = jpaBookRepository.findById(4);
        assertThat(actualBook).isPresent().get().usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        Book expectedBook = new Book(FIRST_BOOK_ID, "new title",
                getAuthor(FIRST_BOOK_ID + 1),getGenre(FIRST_BOOK_ID + 1), getSingleComment(FIRST_BOOK_ID + 1));
        jpaBookRepository.save(expectedBook);
        Optional<Book> actualBook = jpaBookRepository.findById(FIRST_BOOK_ID);
        assertThat(actualBook).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);

    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        jpaBookRepository.deleteById(FIRST_BOOK_ID);
        Optional<Book> deletedBook = jpaBookRepository.findById(FIRST_BOOK_ID);
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
                        dbGenres.get(id - 1),
                        getSingleComment(id - 1)))
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
    private static List<Comment> getSingleComment(int id) {
        return Arrays.asList(new Comment(id, "Comment_" + id, id));
    }


}