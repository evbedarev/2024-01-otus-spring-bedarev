package ru.otus.hw.site;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.book.BookDto;
import ru.otus.hw.dto.book.BookModifyDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.rest.BookRestController;
import ru.otus.hw.services.BookService;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import java.util.List;
import java.util.Optional;

@WebMvcTest({BookRestController.class})
public class BooksRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookService service;

    @Test
    public void shouldReturnCorrectBookList() throws Exception {
        List<Book> books = getDbBooks();
        given(service.findAll()).willReturn(books);
        List<BookDto> expectedResult = books.stream().map(BookDto::toDt0).toList();
        mvc.perform(get("/api/v1/books")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));
    }

    @Test
    public void shouldReturnCorrectBookById() throws Exception {
        Book book = new Book(1L, "BookTitle_1", getDbAuthors().get(0),getDbGenres().get(0));
        given(service.findById(book.getId())).willReturn(Optional.of(book));
        BookDto expectedResult = BookDto.toDt0(book);
        mvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));
    }

    @Test
    public void shouldCorrectAddNewBook() throws Exception {
        Book book = new Book(4L, "BookTitle_4", getDbAuthors().get(1), getDbGenres().get(2));
        given(service.insert(book.getTitle(),book.getAuthor().getId(),book.getGenre().getId()))
                .willReturn(book);
        BookModifyDto bookModifyDto = BookModifyDto.toDto(book);
        String expectedResult = mapper.writeValueAsString(BookDto.toDt0(book));
        String requestContent = mapper.writeValueAsString(bookModifyDto);
        mvc.perform(post("/api/v1/books")
                        .contentType(APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    public void shouldCorrectUpdateBook() throws Exception {
        Book book = new Book(4L, "BookTitle_4_updated", getDbAuthors().get(2), getDbGenres().get(2));
        given(service.update(book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getGenre().getId())).willReturn(book);

        BookModifyDto bookModifyDto = BookModifyDto.toDto(book);
        String expectedResult = mapper.writeValueAsString(BookDto.toDt0(book));
        String requestContent = mapper.writeValueAsString(bookModifyDto);
        mvc.perform(patch("/api/v1/books")
                        .contentType(APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    public void shouldCorrectDeleteBookById() throws Exception {
        mvc.perform(delete("/api/v1/books/1")).andExpect(status().isOk());
        verify(service, times(1)).deleteById(1L);
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
