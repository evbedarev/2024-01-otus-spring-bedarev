package ru.otus.hw.site;


import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookModifyDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String listAllBooks(Model model) {
        List<BookDto> books = bookService.findAll().stream()
                .map(BookDto::toDt0)
                .collect(Collectors.toList());
        model.addAttribute("books",books);
        return "books";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam("id") long id, Model model) {
        Book book = bookService.findById(id).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("modifyBook",BookModifyDto.toDto(book));
        model.addAttribute("bookDto",BookDto.toDt0(book));
        return "edit";
    }

    @PostMapping("/edit")
    public String editBook(@ModelAttribute("modifyBook") BookModifyDto modifyBook,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }
        try {
            bookService.update(modifyBook.getId(),
                    modifyBook.getTitle(),
                    modifyBook.getAuthorId(),
                    modifyBook.getGenreId());
        } catch (EntityNotFoundException ex) {
            checkExceptionAndAddFieldErr(ex,bindingResult);
        }
        if (bindingResult.hasErrors()) {
            return "edit";
        }
        return "redirect:/";

    }

    @GetMapping("/insert")
    public String insertPage(Model model) {
        BookModifyDto modifyBook = new BookModifyDto(0, "title",0,0);
        model.addAttribute("modifyBook",modifyBook);
        return "insert";
    }

    @PostMapping("/insert")
    public String insertNewBook(@Valid  @ModelAttribute("modifyBook") BookModifyDto bookModifyDto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return "insert";
        }
        try {
            bookService.insert(bookModifyDto.getTitle(), bookModifyDto.getAuthorId(), bookModifyDto.getGenreId());
        } catch (EntityNotFoundException ex) {
            checkExceptionAndAddFieldErr(ex,bindingResult);
        }
        if (bindingResult.hasErrors()) {
            return "insert";
        }
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deletePage(@RequestParam("id") long id, Model model) {
        model.addAttribute("bookid",id);
        return "delete";
    }

    @DeleteMapping("/delete")
    public String deleteBook(@RequestParam("id") long id, Model model) {
        bookService.deleteById(id);
        return "redirect:/";
    }

    private void checkExceptionAndAddFieldErr(EntityNotFoundException ex, BindingResult bindingResult) {
        if (ex.getMessage().contains("Author")) {
            bindingResult.addError(new FieldError("book", "authorId",ex.getMessage()));
        }
        if (ex.getMessage().contains("Genre")) {
            bindingResult.addError(new FieldError("book", "genreId",ex.getMessage()));
        }
    }
}
