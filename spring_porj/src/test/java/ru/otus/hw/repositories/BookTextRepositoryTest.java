package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.BookText;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookTextRepositoryTest {

    @Autowired
    private BookTextRepository bookTextRepository;

    @Test
    public void shouldCorrectUpdateMaxPartByBookIdAndPArtNumber() {
        bookTextRepository.updateMaxPartNumByBookId(2L,5);
        Optional<BookText> bookText = bookTextRepository.findByBookIdAndPartNumber(2L,2);
        assertThat(bookText).isPresent().get().hasFieldOrPropertyWithValue("maxPartNumber",5);
    }

    @Test
    public void shouldFindMaxPartByBookId() {
        int maxPartNum = bookTextRepository.findMaxPartNumberByBookId(1L);
        assertThat(maxPartNum).isEqualTo(4);
    }
}
