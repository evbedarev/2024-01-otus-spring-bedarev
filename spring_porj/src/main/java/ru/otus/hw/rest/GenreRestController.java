package ru.otus.hw.rest;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.genre.GenreDto;
import ru.otus.hw.exceptions.EntityAlreadyExistsException;
import ru.otus.hw.exceptions.RelatedEntityException;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GenreRestController {
    private final GenreService genreService;

    public GenreRestController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/api/v1/genres")
    public List<GenreDto> getAllGenres() {
        return genreService.findAll().stream()
                .map(GenreDto::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/api/v1/genres")
    public GenreDto insertNewGenre(@Valid @RequestBody GenreDto genreDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new GenreDto(0,"",bindingResult.getFieldError("name").getDefaultMessage());
        }
        try {
            return GenreDto.toDto(genreService.insert(genreDto.getName()));
        } catch (EntityAlreadyExistsException exception) {
            return new GenreDto(0, "",exception.getMessage());
        }
    }

    @DeleteMapping("/api/v1/genres/{id}")
    public GenreDto deleteNewGenre(@PathVariable("id") long id) {
        try {
            genreService.delete(id);
        } catch (RelatedEntityException exception) {
            return new GenreDto(0, "", exception.getMessage());
        }
        return new GenreDto(0,"deleted");
    }
}
