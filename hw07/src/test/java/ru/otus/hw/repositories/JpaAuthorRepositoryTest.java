package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе data jpa для работы с авторами")
@DataJpaTest
public class JpaAuthorRepositoryTest {

    private final static int EXPECTED_COUNT_AUTHORS = 3;

    private final static int FIRST_AUTHOR_ID = 1;

    @Autowired
    private AuthorRepository jpaRepository;

    @DisplayName("Должен загружать список всех авторов")
    @Test
    void shouldReturnCorretAuthorsList() {
        List<Author> authors = jpaRepository.findAll();
        assertThat(authors).isNotNull().hasSize(EXPECTED_COUNT_AUTHORS)
                .allMatch(s -> !s.getFullName().equals(""))
                .anyMatch(s -> s.getFullName().equals("Author_2") && s.getId() == 2);
    }

    @DisplayName("Должен загружать автора по id")
    @ParameterizedTest
    @MethodSource("getAuthors")
    void shouldReturnCorrectAuthorById() {
        Optional<Author> optionalAuthor = jpaRepository.findById(FIRST_AUTHOR_ID);
        Author expectedAuthor = getAuthor(FIRST_AUTHOR_ID);
        assertThat(optionalAuthor).isPresent().get().usingRecursiveComparison()
                .isEqualTo(expectedAuthor);


    }

    private static List<Author> getAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(JpaAuthorRepositoryTest::getAuthor).toList();
    }

    private static Author getAuthor(int id) {
        return new Author(id, "Author_" + id);
    }
}
