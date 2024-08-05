package ru.otus.hw.rest;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.book.*;
import ru.otus.hw.services.BookTextService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TextBookRestController {

    private final BookTextService bookTextService;

    public TextBookRestController(BookTextService bookTextService) {
        this.bookTextService = bookTextService;
    }

    @PostMapping("/api/v1/text")
    public String addTextForBook(@RequestBody TextBookDto textBookDto) {
        System.out.println("part num %s".formatted(textBookDto.getPartNumber()));
        bookTextService.insertText(textBookDto.getUnformatedText(),
                textBookDto.getPartNumber(),
                textBookDto.getBookId());
        return "ok";
    }

    @GetMapping("/api/v1/text/{id}")
    public TextBookDto getBookText(@PathVariable("id") long id) {
        return bookTextService.findByBookId(id);
    }

    @GetMapping("/api/v1/text/{bookid}/{part}")
    public TextBookDto getBookTextByBookIdAndPartNum(@PathVariable("bookid") long bookid,
                                                     @PathVariable("part") int part) {
        return bookTextService.findByBookIdAndPartNumber(bookid, part);
    }

    @PostMapping("/api/v1/pages/{bookId}")
    public void setPagesOnBook(@PathVariable("bookId") long bookId) {
        bookTextService.setPagesOnBook(bookId);
    }

    @DeleteMapping("/api/v1/text/{bookId}")
    public void deleteTextBook(@PathVariable("bookId") long bookId) {
        bookTextService.removeBookText(bookId);
    }

    private List<String> getAllErrorsFromResult(BindingResult bindingResult) {
        return bindingResult
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }

    private BookDto createErrorDto(BindingResult bindingResult) {
        BookErrorDto errorConverter = new BookErrorDto(true);
        errorConverter.convertStringsToErr(getAllErrorsFromResult(bindingResult));
        return new BookDto(errorConverter);
    }
}
