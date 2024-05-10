package ru.otus.hw.site;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookModifyDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebMvcTest({BookController.class})
public class BooksControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookService service;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @Autowired
    private BookController bookController;

    @Test
    public void shouldReturnCorrectBookList() throws Exception {
        List<Book> books = getDbBooks();
        List<BookDto> expectedResult = books.stream().map(BookDto::toDt0).toList();
        given(service.findAll()).willReturn(books);
        MvcResult result = mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"))
                .andExpect(model().attributeExists("books"))
                .andReturn();
        List<BookDto> actual = (List<BookDto>) result.getModelAndView().getModel().get("books");
        List<Tuple> expectedResultTuples = expectedResult.stream()
                .map(e -> new Tuple(e.getTitle(), e.getId()))
                .collect(Collectors.toList());
        assertThat(actual)
                .extracting(BookDto::getTitle, BookDto::getId)
                .containsExactly(expectedResultTuples.get(0),
                        expectedResultTuples.get(1),
                        expectedResultTuples.get(2));
    }

    @Test
    public void shouldReturnCorrectBookById() throws Exception {
        List<Book> books = getDbBooks();
        BookModifyDto expectedResult = BookModifyDto.toDto(books.getFirst());
        given(service.findById(1)).willReturn(Optional.of(books.getFirst()));
        MvcResult result = mvc.perform(get("/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeExists("modifyBook"))
                .andReturn();
        BookModifyDto actual = (BookModifyDto) result.getModelAndView().getModel().get("modifyBook");
        assertThat(actual).matches(a -> a.getTitle().equals(expectedResult.getTitle()) &&
                a.getId() == expectedResult.getId());
    }

    @Test
    public void shouldCorrectAddNewBook() throws Exception {
        BookModifyDto expectedResult = new BookModifyDto(0, "BookTitle_4", 3, 3);
        mvc.perform(post("/insert")
                        .flashAttr("modifyBook", expectedResult))
                .andExpect(status().is(302));
        verify(service, times(1)).insert(expectedResult.getTitle(),
                expectedResult.getAuthorId(),
                expectedResult.getGenreId());
    }

    @Test
    public void shouldCorrectUpdateBook() throws Exception {
        BookModifyDto expectedResult = new BookModifyDto(2, "BookTitle_2_updated", 3, 3);
        mvc.perform(post("/edit")
                        .flashAttr("modifyBook", expectedResult))
                .andExpect(status().is(302));

        verify(service, times(1)).update(expectedResult.getId(),
                expectedResult.getTitle(),
                expectedResult.getAuthorId(),
                expectedResult.getGenreId());
    }

    @Test
    public void shouldCorrectDeleteBookById() throws Exception {
        mvc.perform(delete("/delete?id=1"))
                .andExpect(status().is(302));
        verify(service, times(1)).deleteById(1L);
    }

    private static List<Author> getDbAuthors() {
        return List.of(new Author(1L, "Author_1"),
                new Author(2L, "Author_2"),
                new Author(3L, "Author_3"));
    }

    private static List<Genre> getDbGenres() {
        return List.of(new Genre(1L, "Genre_1"),
                new Genre(2L, "Genre_2"),
                new Genre(3L, "Genre_3"));
    }

    private static List<Book> getDbBooks() {
        List<Author> dbAuthors = getDbAuthors();
        List<Genre> dbGenres = getDbGenres();
        return List.of(new Book(1L, "BookTitle_1", dbAuthors.get(0), dbGenres.get(0)),
                new Book(2L, "BookTitle_2", dbAuthors.get(1), dbGenres.get(1)),
                new Book(3L, "BookTitle_3", dbAuthors.get(2), dbGenres.get(2)));
    }
}
