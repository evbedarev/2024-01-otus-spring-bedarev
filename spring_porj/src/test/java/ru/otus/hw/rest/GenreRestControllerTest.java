package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.genre.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.GenreService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.List;
import java.util.stream.IntStream;

@WebMvcTest({GenreRestController.class, SecurityConfiguration.class})
public class GenreRestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @MockBean
    private GenreService genreService;

    @Test
    public void shouldReturnAllGenresList() throws Exception {
        List<Genre> genres = getGenreList();
        Mockito.when(genreService.findAll()).thenReturn(genres);
        List<GenreDto> expectedList = genres.stream().map(GenreDto::toDto).toList();
        String expectedString = objectMapper.writeValueAsString(expectedList);
        mockMvc.perform(get("/api/v1/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedString));
    }

    @Test
    public void shouldCreateNewGenre() {
        GenreDto currentDto = GenreDto.toDto(getGenre(2));

    }


    private List<Genre> getGenreList() {
        return IntStream.range(1,5).boxed()
                .map(GenreRestControllerTest::getGenre).toList();
    }

    private static Genre getGenre(int id) {
        return new Genre(id,"Genre_" + id);
    }


}
