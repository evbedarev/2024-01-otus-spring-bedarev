package ru.otus.hw.site;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.otus.hw.dto.BookModifyDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.rest.BookRestController;
import ru.otus.hw.security.MethodSecurityConfiguration;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({BookRestController.class, SecurityConfiguration.class, MethodSecurityConfiguration.class})
public class BookRestControllerSecurityTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Autowired
    private MethodSecurityConfiguration methodSecurityConfiguration;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @WithMockUser(
            username = "user",
            authorities = {"ROLE_USER"}
    )
    @Test
    public void shouldReturnCorrectBookList() throws Exception {
        getAllBooks(status().isOk());
    }

    @Test
    public void shouldFailWhenGetBookList() throws Exception {
        getAllBooks(status().is3xxRedirection());
    }

    @WithMockUser(
            username = "user",
            authorities = {"ROLE_USER"}
    )
    @Test
    public void shouldReturnCorrectBookById() throws Exception {
        getBookById(status().isOk());
    }

    @Test
    public void shouldFailWhenGetBookById() throws Exception {
        getBookById(status().is3xxRedirection());
    }

    @WithMockUser(
            username = "user",
            authorities = {"ROLE_USER"}
    )
    @Test
    public void shouldCorrectAddNewBook() throws Exception {
        insertBook(status().isOk());
    }

    @Test
    public void shouldFailWhenAddNewBook() throws Exception {
        insertBook(status().is3xxRedirection());
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    public void shouldCorrectUpdateBookByAdmin() throws Exception {
        updateBook(status().isOk());
    }

    @Test
    public void shouldFailWhenUpdateBookByUser() throws Exception {
        updateBook(status().is3xxRedirection());
    }

    public void getAllBooks(ResultMatcher resultMatcher) throws Exception {
        List<Book> books = getDbBooks();
        given(bookService.findAll()).willReturn(books);
        mvc.perform(get("/api/v1/books")).andExpect(resultMatcher);
    }
    public void getBookById(ResultMatcher resultMatcher) throws Exception {
        Book book = new Book(1L, "BookTitle_1", getDbAuthors().get(0),getDbGenres().get(0));
        given(bookService.findById(book.getId())).willReturn(Optional.of(book));
        mvc.perform(get("/api/v1/books/1"))
                .andExpect(resultMatcher);
    }

    public void insertBook(ResultMatcher resultMatcher) throws Exception {
        Book book = new Book(4L, "BookTitle_4", getDbAuthors().get(1), getDbGenres().get(2));
        given(bookService.insert(book.getTitle(),book.getAuthor().getId(),book.getGenre().getId()))
                .willReturn(book);
        BookModifyDto bookModifyDto = BookModifyDto.toDto(book);
        String requestContent = mapper.writeValueAsString(bookModifyDto);
        mvc.perform(post("/api/v1/books")
                        .contentType(APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(resultMatcher);
    }

    public void updateBook(ResultMatcher resultMatcher) throws Exception {
        Book book = new Book(4L, "BookTitle_4_updated", getDbAuthors().get(2), getDbGenres().get(2));
        given(bookService.update(book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getGenre().getId())).willReturn(book);
        BookModifyDto bookModifyDto = BookModifyDto.toDto(book);
        String requestContent = mapper.writeValueAsString(bookModifyDto);
        mvc.perform(patch("/api/v1/books")
                        .contentType(APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(resultMatcher);
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
