package ru.otus.hw.site;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.author.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

@Controller
public class AuthorController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    public AuthorController(BookService bookService, AuthorService authorService, GenreService genreService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @GetMapping("/author")
    public String listAllBooks(Model model) {
        return "authors";
    }

    @GetMapping("/author/edit/{id}")
    public String editPage(@PathVariable long id, Model model) {
        Author author = authorService.findById(id);
        model.addAttribute("authorDto", AuthorDto.toDto(author));
        return "edit_author";
    }

    @GetMapping("/author/delete/{id}")
    public String deletePage(@PathVariable long id, Model model) {
        Author author = authorService.findById(id);
        model.addAttribute("authorDto",AuthorDto.toDto(author));
        return "delete_author";
    }

    @GetMapping("/author/insert")
    public String insertAuthorPage(Model model) {
        return "insert_authors.html";
    }
}
