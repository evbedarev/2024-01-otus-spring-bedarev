package ru.otus.hw.site;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.genre.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

@Controller
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/genre")
    public String listAllGenres(Model model) {
        return "genres";
    }

    @GetMapping("/genre/insert")
    public String insertGenre(Model model) {
        return "insert_genres";
    }

    @GetMapping("/genre/delete/{id}")
    public String deletePage(@PathVariable("id") long id, Model model) {
        Genre genre = genreService.findById(id);
        model.addAttribute("genreDto", GenreDto.toDto(genre));
        return "delete_genre";
    }
}
