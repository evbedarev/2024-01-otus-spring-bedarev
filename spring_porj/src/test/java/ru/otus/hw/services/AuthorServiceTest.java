package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.RelatedEntityException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class AuthorServiceTest {

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private AuthorService authorService;

    @BeforeEach
    public void init() {
        Mockito.when(authorRepository.findAll()).thenReturn(getListAuthors());
        Mockito.when(authorRepository.findById(1L))
                .thenReturn(Optional.of(getListAuthors().getFirst()));
        Mockito.when(authorRepository.findByFullName("A2"))
                .thenReturn(Optional.of(getListAuthors().get(1)));
    }

    @Test
    public void shouldCorrectReturnAllAuthors() {
        List<Author> authors = authorService.findAll();
        assertThat(authors).anyMatch(s -> s.getFullName().equals("A1"))
                .anyMatch(s -> s.getFullName().equals("A2"));
    }

    @Test
    public void shouldCorrectReturnAuthorById() {
        Author author = authorService.findById(1L);
        assertThat(author.getFullName()).isEqualTo("A1");
    }

    @Test
    public void shouldThrowExceptionReturnAuthorById() {
        assertThatThrownBy(() -> authorService.findById(3L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void shouldCorrectReturnAuthorByName() {
        Author author = authorService.findByFullName("A2");
        assertThat(author.getId()).isEqualTo(2L);
    }

    @Test
    public void shouldThrowExceptionReturnAuthorByName() {
        assertThatThrownBy(() -> authorService.findByFullName("A4")).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void shouldCorrectDeleteAuthorById() {
        Mockito.when(bookRepository.findFirstByAuthorId(1L))
                .thenReturn(Optional.empty());
        Mockito.when(authorRepository.existsById(1L)).thenReturn(true);
        authorService.delete(1L);
        Mockito.verify(authorRepository,Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void shouldThrowRelatedEntityExceptionWhenDeleteAuthor() {
        Mockito.when(bookRepository.findFirstByAuthorId(1L))
                .thenReturn(Optional.of(getFirstBook()));
        assertThatThrownBy(() -> authorService.delete(1L)).isInstanceOf(RelatedEntityException.class);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenDeleteAuthor() {
        Mockito.when(bookRepository.findFirstByAuthorId(1L))
                .thenReturn(Optional.empty());
        Mockito.when(authorRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> authorService.delete(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void shouldCorrectInsertNewAuthor() {
        Mockito.when(authorRepository.findByFullName("A1")).thenReturn(Optional.empty());
        Mockito.when(authorRepository.save(Mockito.any())).thenReturn(getListAuthors().getFirst());
        Author author = authorService.insert("A1", "AB2");
        assertThat(author.getFullName()).isEqualTo("A1");
        assertThat(author.getAboutAuthor()).isEqualTo("AB1");
    }

    @Test
    public void shouldThrowExceptionWhenInsertAuthor() {
        Mockito.when(authorRepository.findByFullName("A1")).thenReturn(Optional.of(getListAuthors().getFirst()));
        assertThatThrownBy(() -> authorService.insert("A1","AB1"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void shouldCorrectUpdateAuthor() {
        Mockito.when(authorRepository.save(Mockito.any()))
                .thenReturn(getListAuthors().getLast());
        Mockito.when(authorRepository.findById(2L))
                .thenReturn(Optional.of(getListAuthors().getLast()));
        Author author = authorService.update(2L, "A2", "AB2");
        assertThat(author.getId()).isEqualTo(2L);
        assertThat(author.getFullName()).isEqualTo("A2");
        assertThat(author.getAboutAuthor()).isEqualTo("AB2");
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenUpdateAuthor() {
        Mockito.when(authorRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authorService.update(2L,"A1", "AB1"));
    }


    private List<Author> getListAuthors() {
       return Arrays.asList(new Author(1L,"A1", "AB1"),
               new Author(2L, "A2", "AB2"));
    }

    private Book getFirstBook() {
        return new Book(1L,"Title",
                new Author(1l,"A1","B1"),
                new Genre(1L, "G1"));
    }


}
