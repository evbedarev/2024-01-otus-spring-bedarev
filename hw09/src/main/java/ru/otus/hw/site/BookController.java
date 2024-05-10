package ru.otus.hw.site;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import ru.otus.hw.exceptions.EntityNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookModifyDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.stream.Collectors;

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
        List<BookDto> books = bookService.findAll().stream()
                .map(BookDto::toDt0)
                .collect(Collectors.toList());
        model.addAttribute("books",books);
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

    @PostMapping("/edit")
    public String editBook(@ModelAttribute("modifyBook") BookModifyDto modifyBook,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }
        bookService.update(modifyBook.getId(),
                    modifyBook.getTitle(),
                    modifyBook.getAuthorId(),
                    modifyBook.getGenreId());
        return "redirect:/";
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

    @PostMapping("/insert")
    public String insertNewBook(@Valid  @ModelAttribute("modifyBook") BookModifyDto bookModifyDto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return "insert";
        }
        bookService.insert(bookModifyDto.getTitle(), bookModifyDto.getAuthorId(), bookModifyDto.getGenreId());
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deletePage(@PathVariable long id, Model model) {
        model.addAttribute("bookid",id);
        return "delete";
    }

    @DeleteMapping("/delete")
    public String deleteBook(@RequestParam("id") long id, Model model) {
        bookService.deleteById(id);
        return "redirect:/";
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    private ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.badRequest().body("%s. Maybe already was deleted".formatted(ex.getMessage()));
    }
}
