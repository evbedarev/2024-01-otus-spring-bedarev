package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.book.TextBookDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.BookTextService;
import ru.otus.hw.services.LoadFile;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest({TextBookRestController.class, SecurityConfiguration.class})
public class TextBookRestControllerTest {


    private final static String TEST_TEXT_FILE = "test_row_file.txt";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @MockBean
    private LoadFile loadFile;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookTextService bookTextService;

    @Test
    public void shouldCallBookTextServiceWithArgsWhenPostTextRest() throws Exception {
        TextBookDto textBookDto = getTextDto();
        String requestContent = objectMapper.writeValueAsString(textBookDto);
        mockMvc.perform(post("/api/v1/text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().isOk());
        verify(bookTextService, times(1)).insertText(textBookDto.getUnformatedText(),
                textBookDto.getPartNumber(),
                textBookDto.getBookId());
    }

    @Test
    public void shouldReturnCorrectTextBookDtoWherCallGetTextApi() throws Exception {
        TextBookDto textBookDto = getTextDto();
        when(bookTextService.findByBookId(1L)).thenReturn(textBookDto);
        mockMvc.perform(get("/api/v1/text/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(textBookDto)));
    }

    @Test
    public void shouldCorrectGetTextByBookIdAndPartNumber() throws Exception {
        TextBookDto textBookDto = getTextDto();
        when(bookTextService
                .findByBookIdAndPartNumber(textBookDto.getBookId(), textBookDto.getPartNumber()))
                .thenReturn(textBookDto);
        mockMvc.perform(get("/api/v1/text/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(textBookDto)));
    }

    @Test
    public void shouldCallSetPagesInBookTextService() throws Exception {
        mockMvc.perform(post("/api/v1/pages/1"))
                .andExpect(status().isOk());
        verify(bookTextService, times(1)).setPagesOnBook(1L);
    }

    @Test
    public void shouldCallRemoveBookTextMethodInBookTextService() throws Exception {
        mockMvc.perform(delete("/api/v1/text/1"))
                .andExpect(status().isOk());
        verify(bookTextService, times(1)).removeBookText(1L);
    }

    @Test
    public void shouldReturnCorrectPagesSize() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(TEST_TEXT_FILE);
        MockMultipartFile multipartFile = new MockMultipartFile("file", TEST_TEXT_FILE,
                MediaType.TEXT_PLAIN_VALUE, inputStream.readAllBytes());
        mockMvc.perform(multipart("/api/v1/text/upload/1").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    private TextBookDto getTextDto() {
        List<String> unfText = new ArrayList<>();
        unfText.add("some string");
        return new TextBookDto(unfText, 1, 2,
                2, 1L, unfText);
    }
}
