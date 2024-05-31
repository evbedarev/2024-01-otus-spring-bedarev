package ru.otus.hw.site;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest({BookController.class})
@ExtendWith({SpringExtension.class})
public class BooksControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @BeforeEach
    public void init() {
        Mockito.when(bookService.findById(1)).thenReturn(Optional.of(getBook()));
        Mockito.when(authorService.findAll()).thenReturn(Arrays.asList(new Author(1L, "1")));
        Mockito.when(genreService.findAll()).thenReturn(Arrays.asList(new Genre(1L, "1")));
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ADMIN_ROLE"}
    )
    @Test
    public void shouldCorrectGetRootPage() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());

    }

    @Test
    public void shouldFailWhenGetRootPage() throws Exception {
        mvc.perform(get("/")).andExpect(status().isUnauthorized());

    }

    @WithMockUser(
            username = "admin",
            authorities = {"ADMIN_ROLE"}
    )
    @Test
    public void shouldCorrectGetEditPage() throws Exception {
        mvc.perform(get("/edit/1")).andExpect(status().isOk());
    }

    @Test
    public void shouldFailWhenGetEditPage() throws Exception {
        mvc.perform(get("/edit/1")).andExpect(status().isUnauthorized());
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ADMIN_ROLE"}
    )
    @Test
    public void shouldCorrectGetInsertPage() throws Exception {
        mvc.perform(get("/insert")).andExpect(status().isOk());
    }

    @Test
    public void shouldFailWhenGetInsertPage() throws Exception {
        mvc.perform(get("/insert")).andExpect(status().isUnauthorized());
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ADMIN_ROLE"}
    )
    @Test
    public void shouldCorrectGetDeletePage() throws Exception {
        mvc.perform(get("/delete/1")).andExpect(status().isOk());
    }

    @Test
    public void shouldFailWhenGetDeletePage() throws Exception {
        mvc.perform(get("/delete/1")).andExpect(status().isUnauthorized());
    }

    private final Book getBook() {
        return new Book(1L, "ttl", new Author(1L, "1"), new Genre(1L, "Some_title"));
    }


}
