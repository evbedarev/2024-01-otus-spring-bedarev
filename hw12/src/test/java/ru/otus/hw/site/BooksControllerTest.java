package ru.otus.hw.site;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest({BookController.class, SecurityConfiguration.class})
public class BooksControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SecurityConfiguration securityConfiguration;

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
            authorities = {"ROLE_ADMIN"}
    )
    @DisplayName("Проверяет доступные эндпоинты для аутентифицированного пользователя")
    @ParameterizedTest
    @MethodSource("getUris")
    void shouldCorrectGetPages(String uri) throws Exception {
        mvc.perform(get(uri)).andExpect(status().isOk());
    }

    @DisplayName("Проверяет что для неаутентифицированного пользователя выдает редирект")
    @ParameterizedTest
    @MethodSource("getUris")
    void shouldFailWhenGetPages(String uri) throws Exception {
        mvc.perform(get(uri)).andExpect(status().is3xxRedirection());
    }


    private static List<String> getUris() {
        return Arrays.asList("/","/edit/1","/insert","/delete/1");
    }

    private final Book getBook() {
        return new Book(1L, "ttl", new Author(1L, "1"), new Genre(1L, "Some_title"));
    }
}
