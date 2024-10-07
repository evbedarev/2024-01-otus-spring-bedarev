package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Bookmark;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@DisplayName("Тест сервиса закладок")
@DataJpaTest
public class BookmarkRepositoryTest {

    @Autowired
    private BookmarksRepository bookmarksRepository;


    @Test
    public void shouldReturnCorrectBookmarkByBookIdAndUsername() {
        Optional<Bookmark> bookmark = bookmarksRepository.findByBookIdAndUsername(2,"madjo");
        assertThat(bookmark.get().getUsername()).isEqualTo("madjo");
        assertThat(bookmark.get().getBookId()).isEqualTo(2);
    }

    @Test
    public void shouldCorrectDeleteBookmarkByBookIdAndUsername() {
        bookmarksRepository.deleteByBookIdAndUsername(1,"admin");
        Optional<Bookmark> bookmark = bookmarksRepository.findByBookIdAndUsername(1,"admin");
        assertThat(bookmark.isPresent()).isEqualTo(false);
        bookmark = bookmarksRepository.findByBookIdAndUsername(1,"madjo");
        assertThat(bookmark.isPresent()).isEqualTo(true);

    }


}
