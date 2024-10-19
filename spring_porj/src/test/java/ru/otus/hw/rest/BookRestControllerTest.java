package ru.otus.hw.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import ru.otus.hw.dto.author.AuthorDto;
import ru.otus.hw.dto.book.BookDto;
import ru.otus.hw.dto.book.BookErrorDto;
import ru.otus.hw.dto.book.BookModifyDto;
import ru.otus.hw.dto.book.BookPagesDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest({BookRestController.class, SecurityConfiguration.class})
public class BookRestControllerTest {

    private static final int PAGE_SIZE = 5;

    private static final String SORTED_BY = "id";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityConfiguration securityConfigurationl;

    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    private List<Book> bookList;

    @BeforeEach
    public void init() {
        bookList = getListBooks();
    }


    @Test
    public void shouldCirregtGetAllBooks() throws Exception {
        Mockito.when(bookService.findAll()).thenReturn(bookList);
        List<BookDto> bookDtos = bookList.stream().map(BookDto::toDt0).toList();
        String response = objectMapper.writeValueAsString(bookDtos);
        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }

    @Test
    public void shouldCorrectGetBookById() throws Exception {
        Optional<Book> actualBook = Optional.of(getListBooks().get(1));
        Mockito.when(bookService.findById(1L)).thenReturn(actualBook);
        BookDto bookDto = BookDto.toDt0(actualBook.get());
        String expectedContent = objectMapper.writeValueAsString(bookDto);
        mockMvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedContent));
    }

    @Test
    public void shouldCorrectReturnPageWithBooks() throws Exception {
        Page<Book> bookPage = new PageImpl<>(bookList);
        Mockito.when(bookService.findAllInPages(1)).thenReturn(bookPage);
        String expectedList = objectMapper.writeValueAsString(new BookPagesDto(bookList,bookPage.getTotalPages()));
        mockMvc.perform(get("/api/v1/books/pages/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedList));
    }

    @Test
    public void shouldCorrectReturnBookByTitle() throws Exception {
        String requestString = objectMapper.writeValueAsString(BookModifyDto.toDto(getBook(2)));
        Mockito.when(bookService.findByTitle("title2")).thenReturn(Optional.of(getBook(2)));
        BookDto expectedDto = BookDto.toDt0(getBook(2));
        checkPostResult("/api/v1/books/find/title", requestString,
                objectMapper.writeValueAsString(expectedDto));
    }

    @Test
    public void shouldThrowErrorOnRequestFindByTitle() throws Exception {
        String requestString = objectMapper.writeValueAsString(BookModifyDto.toDto(getBook(2)));
        Mockito.when(bookService.findByTitle("title2")).thenReturn(Optional.empty());
        BookErrorDto bookErrorDto = new BookErrorDto(true);
        bookErrorDto.setTitleError("Title 'title2' not found");
        BookDto expectedDto = new BookDto(bookErrorDto);
        checkPostResult("/api/v1/books/find/title", requestString,
                objectMapper.writeValueAsString(expectedDto));
    }

    @Test
    public void shouldCorrectFindBooksByAuthorFullName() throws Exception {
        String authorFullName = "Михаил Юрьевич Лермонтов";
        Mockito.when(bookService.findByAuthorFullName(authorFullName)).thenReturn(getListBooks());
        AuthorDto authorDto = new AuthorDto();
        authorDto.setFullName(authorFullName);
        String requestContent = objectMapper.writeValueAsString(authorDto);
        checkPostResult("/api/v1/books/find/author", requestContent,
                objectMapper.writeValueAsString(getListBooks()));
    }

    @Test
    public void shouldCorrectCreateNewBook() throws Exception {
        Book book = getBook(1);
        Mockito.when(bookService.insert(book.getTitle(),
                book.getAuthor().getId(),
                book.getGenre().getId())).thenReturn(book);
        BookModifyDto bookModifyDto = BookModifyDto.toDto(book);
        String requestContent = objectMapper.writeValueAsString(bookModifyDto);
        String expectedContent = objectMapper.writeValueAsString(BookDto.toDt0(book));
        checkPostResult("/api/v1/books", requestContent, expectedContent);
    }

    @Test
    public void shouldReturnErrorDtoWhenCreateNewBook() throws Exception {
        Book book = new Book(1L, "t", getBook(1).getAuthor(), getBook(1).getGenre());
        BookModifyDto bookModifyDto = BookModifyDto.toDto(book);
        String requestContent = objectMapper.writeValueAsString(bookModifyDto);
        String expectedContent = objectMapper.writeValueAsString(createErrorDto("Title input more then 2 chars"));
        checkPostResult("/api/v1/books", requestContent, expectedContent);
    }


    private List<Book> getListBooks() {
        return IntStream.range(1,5).boxed()
                .map(BookRestControllerTest::getBook).toList();
    }

    private void checkPostResult(String uri, String requestContent, String expectedContent) throws Exception {
        mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedContent));

    }

    private static Book getBook(long id) {
        Author author = new Author(id,"name" + id,"info" + id);
        Genre genre = new Genre(id,"genre" + id);
        return new Book(id,"title" + id, author, genre);
    }

    private BookDto createErrorDto(String error) {
        BookErrorDto errorDto = new BookErrorDto(true);
        errorDto.setTitleError(error);
        return new BookDto(errorDto);
    }
}
