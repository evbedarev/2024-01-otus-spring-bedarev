package ru.otus.hw.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.otus.hw.dto.book.*;
import ru.otus.hw.models.BookText;
import ru.otus.hw.services.BookTextService;
import ru.otus.hw.services.LoadFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TextBookRestController {

    private final static Logger logger = LoggerFactory.getLogger(TextBookRestController.class);

    private final BookTextService bookTextService;

    private final LoadFile loadFile;

    public TextBookRestController(BookTextService bookTextService, LoadFile loadFile) {
        this.bookTextService = bookTextService;
        this.loadFile = loadFile;
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

    @PostMapping("/api/v1/text/upload/{book_id}")
    public int uploadTextFile(@RequestParam("file") MultipartFile file,
                               @PathVariable("book_id") long book_id) throws IOException {
        logger.info("Load file with name %s".formatted(file.getName()));
        logger.info("Load file with size %s".formatted(Long.toString(file.getSize())));
        logger.info("Load file with content type %s".formatted(file.getContentType()));
        InputStream inputStream = file.getInputStream();
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\n");
        List<BookText> bookTextList = loadFile.load(scanner, book_id);
        bookTextService.setPagesOnBook(book_id);
        return bookTextList.size();
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
