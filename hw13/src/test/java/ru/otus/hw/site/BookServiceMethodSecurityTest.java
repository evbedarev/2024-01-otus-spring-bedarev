package ru.otus.hw.site;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.BookModifyDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.rest.BookRestController;
import ru.otus.hw.security.MethodSecurityConfiguration;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;
import java.util.Optional;


@WebMvcTest({SecurityConfiguration.class, BookServiceImpl.class, MethodSecurityConfiguration.class, BookRestController.class})
public class BookServiceMethodSecurityTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Autowired
    private MethodSecurityConfiguration methodSecurityConfiguration;

    @Autowired
    private BookService bookService;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private BookRepository bookRepository;

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    public void shouldCorrectGetAllBooksAsAdmin() throws Exception {
        prepareMockUpdate();
        BookModifyDto bookModifyDto = BookModifyDto.toDto(getDbBooks().getFirst());
        String requestContent = mapper.writeValueAsString(bookModifyDto);
        mvc.perform(patch("/api/v1/books")
                        .contentType(APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().isOk());
    }

    @WithMockUser(
            username = "user",
            authorities = {"ROLE_USER"}
    )
    @Test
    public void shouldFailWhenUpdateBookAsUser() throws Exception {
        BookModifyDto bookModifyDto = BookModifyDto.toDto(getDbBooks().getFirst());
        String requestContent = mapper.writeValueAsString(bookModifyDto);
        mvc.perform(patch("/api/v1/books")
                        .contentType(APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().is4xxClientError());
    }


    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    public void shouldCorrectDeleteBookAsAdmin() throws Exception {
        Mockito.when(bookRepository.existsById(Mockito.any())).thenReturn(true);
        mvc.perform(delete("/api/v1/books/1")).andExpect(status().isOk());
    }


    @WithMockUser(
            username = "user",
            authorities = {"ROLE_USER"}
    )
    @Test
    public void shouldFailWhenDeleteBookAsUser() throws Exception {
        Mockito.when(bookRepository.existsById(Mockito.any())).thenReturn(true);
        mvc.perform(delete("/api/v1/books/1")).andExpect(status().is4xxClientError());
    }

    private void prepareMockUpdate() {
        Mockito.when(authorRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(getDbAuthors().getFirst()));
        Mockito.when(genreRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(getDbGenres().getFirst()));
        Mockito.when(bookRepository.save(Mockito.any())).thenReturn(getDbBooks().getFirst());
    }

    private static List<Author> getDbAuthors() {
        return List.of(new Author(1L, "Author_1"),
                new Author(2L,"Author_2"),
                new Author(3L, "Author_3"));
    }

    private static List<Genre> getDbGenres() {
        return List.of(new Genre(1L,"Genre_1"),
                new Genre(2L, "Genre_2"),
                new Genre(3L,"Genre_3"));
    }

    private static List<Book> getDbBooks() {
        List<Author> dbAuthors = getDbAuthors();
        List<Genre> dbGenres = getDbGenres();
        return List.of(new Book(1L,"BookTitle_1",dbAuthors.get(0),dbGenres.get(0)),
                new Book(2L, "BookTitle_2", dbAuthors.get(1), dbGenres.get(1)),
                new Book(3L,"BookTitle_3",dbAuthors.get(2),dbGenres.get(2)));
    }
}
