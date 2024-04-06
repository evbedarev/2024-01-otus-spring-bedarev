package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Репозиторий на основе jpa для работы с жанрами")
@DataJpaTest
@Import({JpaGenreRepository.class})
public class JpaGenreRepositoryTest {

    private final static int GENRES_COUNT = 3;

    private final static long ID_FIRST_GENRE = 1L;
    @Autowired
    private GenreRepository jpaGenreRepository;

    @DisplayName("Должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        List<Genre> genres = jpaGenreRepository.findAll();
        assertThat(genres).isNotNull().hasSize(GENRES_COUNT)
                .allMatch(g -> !g.getName().equals(""))
                .anyMatch(g -> g.getName().equals(getGenres().get(2).getName()))
                .anyMatch(g -> g.getName().equals(getGenres().get(1).getName()))
                .anyMatch(g -> g.getName().equals(getGenres().get(0).getName()));

    }

    @DisplayName("Должен загружать жанр по id")
    @ParameterizedTest
    @MethodSource("getGenres")
    void shouldReturnCorrectGenreById(Genre expectedGenre) {
        Optional<Genre> genre = jpaGenreRepository.findById(ID_FIRST_GENRE);
        assertThat(genre).isPresent().get().usingRecursiveComparison()
                .isEqualTo(getGenre(ID_FIRST_GENRE));
    }

    private static List<Genre> getGenres() {
        return IntStream.range(1, 4).boxed()
                .map(JpaGenreRepositoryTest::getGenre).toList();
    }

    private static Genre getGenre(long id) {
        return new Genre(id, "Genre_" + id);
    }
}
