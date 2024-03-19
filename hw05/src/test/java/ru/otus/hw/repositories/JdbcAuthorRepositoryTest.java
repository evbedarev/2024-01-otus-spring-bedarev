package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе jdbc для работы с авторами")
@JdbcTest
@Import({JdbcAuthorRepository.class})
public class JdbcAuthorRepositoryTest {

    @Autowired
    JdbcAuthorRepository jdbcRepository;

    private List<Author> authors;

    @BeforeEach
    void init() {
        authors = getAuthors();
    }

    @DisplayName("Должен загружать список всех авторов")
    @Test
    void shouldReturnCorretAuthorsList() {
        List<Author> expectedAuthors = getAuthors();
        List<Author> actualAuthors = jdbcRepository.findAll();
        assertThat(actualAuthors).containsExactlyElementsOf(expectedAuthors);
        actualAuthors.forEach(System.out::println);
    }

    @DisplayName("Должен загружать автора по id")
    @ParameterizedTest
    @MethodSource("getAuthors")
    void shouldReturnCorrectAuthorById(Author expectedAuthor) {
        Optional<Author> actualAuthor = jdbcRepository.findById(expectedAuthor.getId());
        assertThat(actualAuthor).isPresent()
                .get()
                .isEqualTo(expectedAuthor);
    }

    private static List<Author> getAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id)).toList();
    }

}
