package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Bookmark;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookmarksRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class BookmarkServiceTest {

    @MockBean
    private BookmarksRepository bookmarksRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private Authentication authentication;

    @Autowired
    private BookmarksService bookmarksService;

    @BeforeEach
    public void init() {
        Mockito.when(authentication.getName()).thenReturn("madjo");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void shouldCorrectInsertBookmark() {
        Mockito.when(bookRepository.existsById(1L)).thenReturn(true);
        Bookmark expectedBookmark = createBookmark();
        Mockito.when(bookmarksRepository.save(Mockito.any())).thenReturn(expectedBookmark);
        Bookmark currentBookmark= bookmarksService.insertBookmark(1L,2,1);
        assertThat(currentBookmark).isEqualTo(expectedBookmark);
    }

    @Test
    public void shouldThrowException() {
        Mockito.when(bookRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> bookmarksService.insertBookmark(1L,2,1))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void shouldFindBookmarkByIdAndUsername() {
        Optional<Bookmark> expectedBookmark = Optional.of(createBookmark());
        Mockito.when(bookmarksRepository.findByBookIdAndUsername(1L,"madjo"))
                .thenReturn(expectedBookmark);
        Optional<Bookmark> resultBookmark = bookmarksService.findBookmark(1L);
        assertThat(expectedBookmark.get()).isEqualTo(resultBookmark.get());
    }

    @Test
    public void shouldDeleteBookmarkByBookIdAndUsername() {
        Bookmark bookmark = createBookmark();
        Mockito.when(bookmarksRepository.findByBookIdAndUsername(1L,"madjo"))
                .thenReturn(Optional.of(bookmark));
        String username = bookmarksService.deleteBookmarkByBookIdAndUsername(1L);
        Mockito.verify(bookmarksRepository, Mockito.times(1))
                        .deleteByBookIdAndUsername(1L, "madjo");
        assertThat(username).isEqualTo("madjo");
    }

    @Test
    public void shouldVoidDeleteMethodInBookmarkRepository() {
        bookmarksService.deleteAllBookmarksByBookId(1L);
        Mockito.verify(bookmarksRepository,Mockito.times(1)).deleteByBookId(1L);
    }

    private Bookmark createBookmark() {
        return new Bookmark(
                1L, "madjo", 1L, 1, 2);
    }
}
