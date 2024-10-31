package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.bookmarks.BookmarkDto;
import ru.otus.hw.models.Bookmark;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.BookmarksService;

import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({BookmarkRestController.class, SecurityConfiguration.class})
public class BookMarkRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @MockBean
    private BookmarksService bookmarksService;

    @Test
    public void shouldCorrectCreateNewBookmark() throws Exception {
        Bookmark bookmark = createBookmark();
        Mockito.when(bookmarksService.insertBookmark(bookmark.getBookId(),
                bookmark.getCurPage(),bookmark.getPartNum())).thenReturn(bookmark);
        String expectedContent = objectMapper.writeValueAsString(BookmarkDto.toDto(bookmark));
        mockMvc.perform(post("/api/v1/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedContent))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedContent));

    }

    @Test
    public void shouldCorrectReturnBookbark() throws Exception {
        Bookmark bookmark = createBookmark();
        Mockito.when(bookmarksService.findBookmark(1L)).thenReturn(Optional.of(bookmark));
        String expectedResponse = objectMapper.writeValueAsString(BookmarkDto.toDto(bookmark));
        mockMvc.perform(get("/api/v1/bookmarks/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    private Bookmark createBookmark() {
        return new Bookmark(
                1L, "madjo", 1L, 1, 2);
    }
}

