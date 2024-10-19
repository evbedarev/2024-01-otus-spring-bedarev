package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookText;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookTextRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootTest
public class LoadTextFileServiceTest {

    private final static String TEST_TEXT_FILE = "test_row_file.txt";
    private final static String TEST_LONG_LINES_TEXT_FILE = "test_long_lines.txt";

    private LoadFile loadFile;

    @MockBean
    private BookText bookText;

    @MockBean
    private BookTextRepository bookTextRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookTextService bookTextService;

    @MockBean
    private BookmarksService bookmarksService;

    @BeforeEach
    public void init() {
        Mockito.when(bookTextRepository.save(Mockito.any())).thenReturn(
                new BookText(1L,"text",1,getBook().get()));
        Mockito.when(bookRepository.findById(1L)).thenReturn(getBook());
    }

    @Test
    public void shouldLoadTextFileAndCreateListWithCorrectSize() {
        loadFile = new LoadTextFileImpl(bookTextRepository,bookTextService, bookRepository, bookmarksService);
        List<BookText> bookTextList = readTextFile(TEST_TEXT_FILE);
        assertThat(bookTextList.size()).isEqualTo(1);
    }

    @Test
    public void shouldLoadTextFileAndReturnCorrectCharsRowsPagesCount() {
        loadFile = new LoadTextFileImpl(bookTextRepository,bookTextService, bookRepository, bookmarksService);
        List<BookText> bookTextList = readTextFile(TEST_LONG_LINES_TEXT_FILE);
        int countChars = 0;
        int countPages = 0;
        int countRows = 0;
        for (BookText bt: bookTextList) {
            countChars = countChars + getCountChars(Arrays.asList(bt.getBookText().split("\n")));
            countPages = countPages + bt.getMaxPage();
            countRows = countRows + bt.getBookText().split("\n").length;
            System.out.println(bt.getBookText());
        }
        assertThat(countChars).isEqualTo(27833);
        assertThat(countPages).isEqualTo(9);
        assertThat(countRows).isEqualTo(471);
    }

    private int getCountChars(List<String> lines) {
        int countChars = 0;
        for (String line: lines) {
            countChars = countChars  + line.length();
        }
        return countChars;
    }

    private List<BookText> readTextFile(String filepath) {
        InputStream inputStream = getFileFromResourceAsStream(filepath);
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\n");
        return loadFile.load(scanner, 1);
    }


    private InputStream getFileFromResourceAsStream(String filename) {
        ClassLoader classLoader =  getClass().getClassLoader();
        return classLoader.getResourceAsStream(filename);
    }

    private Optional<Book> getBook() {
        Author author = new Author(1,"name","info");
        Genre genre = new Genre(1,"genre");
        return Optional.of(new Book(1,"title", author, genre));
    }
}
