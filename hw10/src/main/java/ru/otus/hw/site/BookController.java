package ru.otus.hw.site;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.dto.BookModifyDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    public BookController(BookService bookService, AuthorService authorService, GenreService genreService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @GetMapping("/")
    public String listAllBooks(Model model) {
        return "books";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable long id, Model model) {
        Book book = bookService.findById(id).orElseThrow(EntityNotFoundException::new);
        List<Author> authors = authorService.findAll();
        List<Genre> genres = genreService.findAll();
        model.addAttribute("modifyBook",BookModifyDto.toDto(book));
        model.addAttribute("authorsList",authors);
        model.addAttribute("genresList",genres);
        return "edit";
    }

    @GetMapping("/insert")
    public String insertPage(Model model) {
        BookModifyDto modifyBook = new BookModifyDto(0, "title",0,0);
        List<Author> authors = authorService.findAll();
        List<Genre> genres = genreService.findAll();
        model.addAttribute("modifyBook",modifyBook);
        model.addAttribute("authorsList",authors);
        model.addAttribute("genresList",genres);
        return "insert";
    }

    @GetMapping("/delete/{id}")
    public String deletePage(@PathVariable long id, Model model) {
        model.addAttribute("bookid",id);
        return "delete";
    }
}
