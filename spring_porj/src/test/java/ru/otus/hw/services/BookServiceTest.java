package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookTextRepository bookTextRepository;

    @MockBean
    private BookmarksRepository bookmarksRepository;


    @BeforeEach
    public void init() {
        Mockito.when(bookmarksRepository.existsByBookId(1L)).thenReturn(false);
        Mockito.when(bookTextRepository.existsByBookId(1L)).thenReturn(false);
    }

    @Test
    public void shouldThrowEnitityNotFoundException() {
        Mockito.when(bookRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> bookService.deleteById(1L)).isInstanceOf(EntityNotFoundException.class);

        Mockito.when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.insert("w", 1L, 1L))
                .isInstanceOf(EntityNotFoundException.class);

        Mockito.when(authorRepository.findById(1L)).thenReturn(getAuthor());
        Mockito.when(genreRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.insert("w", 1L, 1L))
                .isInstanceOf(EntityNotFoundException.class);
    }


    private Optional<Author> getAuthor() {
        return Optional.of(new Author(1, "w", "w"));
    }



}
