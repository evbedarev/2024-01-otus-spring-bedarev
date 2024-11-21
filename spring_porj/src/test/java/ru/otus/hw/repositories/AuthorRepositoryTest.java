package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе jpa для работы с авторами")
@DataJpaTest
public class AuthorRepositoryTest {

    private final static String AUTHOR_NAME = "Александр Сергеевич Пушкин";

    private final static long AUTHOR_ID = 2L;

    private final static String ABOUT_AUTHOR = "Александр Пушкин – великий русский поэт, прозаик," +
            " драматург, один из самых авторитетных литературных деятелей первой трети XIX века.";

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void shouldReturnCorrectAuthorById() {
        Optional<Author> resultAuthor = authorRepository.findById(AUTHOR_ID);
        assertThat(resultAuthor.get().getFullName()).isEqualTo(AUTHOR_NAME);
    }

    @Test
    public void shouldReturnCorrectAuthorByFullName() {
        Optional<Author> resultAuthor = authorRepository.findByFullName(AUTHOR_NAME);
        assertThat(resultAuthor.get().getId()).isEqualTo(AUTHOR_ID);
        assertThat(resultAuthor.get().getAboutAuthor()).isEqualTo(ABOUT_AUTHOR);
    }

    private static List<Author> getAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(AuthorRepositoryTest::getAuthor).toList();
    }

    private static Author getAuthor(long id) {
        return new Author(id, "Author_" + id, "About_" + 1);
    }
}
