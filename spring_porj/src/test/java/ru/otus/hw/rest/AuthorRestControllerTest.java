package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.otus.hw.dto.author.AuthorDto;
import ru.otus.hw.dto.author.AuthorErrorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({AuthorRestController.class, SecurityConfiguration.class})
public class AuthorRestControllerTest {

    private static final String TEST_AUTHOR_NAME = "Author1";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService authorService;

    @Test
    public void shouldCorrectFindAuthorByFullName() throws Exception {
        Mockito.when(authorService.findByFullName(TEST_AUTHOR_NAME)).thenReturn(
                new Author(1, TEST_AUTHOR_NAME, "AB1"));
        AuthorDto authorDto = AuthorDto.toDto(new Author(1, TEST_AUTHOR_NAME, "AB1"));
        String requestContent = objectMapper.writeValueAsString(authorDto);
        mockMvc.perform(post("/api/v1/authors/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(authorDto)));
    }

    @Test
    public void shouldCorrectListAllAuthors() throws Exception {
        List<Author> authors = getListAuthors();
        List<AuthorDto> authorDtos = authors.stream().map(AuthorDto::toDto).toList();
        Mockito.when(authorService.findAll()).thenReturn(getListAuthors());
        mockMvc.perform(get("/api/v1/authors"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(authorDtos)));

    }

    @Test
    public void shouldCorrectReturnAuthorById() throws Exception {
        Mockito.when(authorService.findById(1L)).thenReturn(getListAuthors().get(0));
        AuthorDto authorDto = AuthorDto.toDto(getListAuthors().get(0));
        mockMvc.perform(get("/api/v1/authors/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(authorDto)));

    }

    @Test
    public void shouldCreateNewAuthor() throws Exception {
        Author requestAuthor = getListAuthors().get(1);
        AuthorDto expectedResult = AuthorDto.toDto(requestAuthor);
        Mockito.when(authorService.insert(requestAuthor.getFullName(), requestAuthor.getAboutAuthor()))
                .thenReturn(requestAuthor);
        checkUpdateInsertControllersAnswer(requestAuthor, expectedResult, post("/api/v1/authors"));
    }

    @Test
    public void shouldReturnErrorDtoWhenCreateNewAuthor() throws Exception {
        checkThatReturnErrorDto(post("/api/v1/authors"));
    }

    @Test
    public void shouldCorrectUpdateAuthor() throws Exception {
        Author requestAuthor = getListAuthors().get(1);
        AuthorDto expectedResult = AuthorDto.toDto(requestAuthor);
        Mockito.when(authorService.update(requestAuthor.getId(),
                        requestAuthor.getFullName(),
                        requestAuthor.getAboutAuthor()))
                .thenReturn(requestAuthor);
        checkUpdateInsertControllersAnswer(requestAuthor, expectedResult, patch("/api/v1/authors"));
    }

    @Test
    public void shouldReturnErrorDtoWhenUpdateAuthor() throws Exception {
        checkThatReturnErrorDto(patch("/api/v1/authors"));
    }

    @Test
    public void shouldCorrectDeleteAuthorById() throws Exception {
        mockMvc.perform(delete("/api/v1/authors/1"))
                .andExpect(status().isOk());
        verify(authorService, times(1)).delete(1L);

    }

    private void checkUpdateInsertControllersAnswer(Author requestAuthor,
                                                    AuthorDto expectedResult,
                                                    MockHttpServletRequestBuilder requestBuilder) throws Exception {
        String authorContent = objectMapper.writeValueAsString(requestAuthor);
        mockMvc.perform(requestBuilder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorContent))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
    }

    private void checkThatReturnErrorDto(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        Author requestAuthor = getListAuthors().get(0);
        AuthorErrorDto authorErrorDto = new AuthorErrorDto(true);
        authorErrorDto.setFullNameError("Full name input more then 5 chars");
        AuthorDto expectedResult = new AuthorDto(authorErrorDto);
        String authorContent = objectMapper.writeValueAsString(requestAuthor);
        mockMvc.perform(requestBuilder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorContent))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
    }

    private List<Author> getListAuthors() {
        return Arrays.asList(new Author(1L, "A1", "AB1"),
                new Author(2L, "AAAAA2", "AB2"),
                new Author(3L, "A3", "AB3"));
    }

}
