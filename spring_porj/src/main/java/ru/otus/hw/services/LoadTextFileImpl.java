package ru.otus.hw.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookText;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookTextRepository;

import java.util.*;

@Service
public class LoadTextFileImpl implements LoadFile {

    private static final Logger logger = LoggerFactory.getLogger(LoadTextFileImpl.class);

    private final BookTextRepository bookTextRepository;

    private final BookTextService bookTextService;

    private final BookRepository bookRepository;

    private final BookmarksService bookmarksService;


    private static final int COUNT_CHARS_IN_ROWS = 120;

    public LoadTextFileImpl(BookTextRepository bookTextRepository, BookTextService bookTextService, BookRepository bookRepository, BookmarksService bookmarksService) {
        this.bookTextRepository = bookTextRepository;
        this.bookTextService = bookTextService;
        this.bookRepository = bookRepository;
        this.bookmarksService = bookmarksService;
    }

    @Override
    @Transactional
    public List<BookText> load(Scanner scanner, long book_id) {
        bookTextService.removeBookText(book_id);
        bookmarksService.deleteAllBookmarksByBookId(book_id);
        List<String> textList = new ArrayList<>();
        Optional<Book> book = bookRepository.findById(book_id);
        if (book.isEmpty()){
            logger.info("Book with id %d not found".formatted(book_id));
            return new ArrayList<>();
        }
        while (scanner.hasNext()) {
            String line = scanner.next();
            if (line.length() > 120) {
                textList.addAll(splitLongLine(line));
            } else {
                textList.add(line);
            }
        }
        logger.debug("LIST SIZE + " + textList.size());
        List<BookText> bookTextList = splitOnPages(textList, book.get());
        for (BookText text: bookTextList) {
            int maxPage = getCountPages(text);
            if (maxPage == 0)
                maxPage = 1;
            logger.debug("MAXPAGE = " + maxPage);
            text.setMaxPage(maxPage);
            bookTextRepository.save(text);
        }
        return bookTextList;
    }

    private int getCountPages(BookText bookText) {
        List<String> splitedPages = Arrays.asList(bookText.getBookText().split("\n"));
        return  ((splitedPages.size()/50) + (splitedPages.size() > 201 ? 1 : 0));
    }

    private List<BookText> splitOnPages(List<String> textList, Book book) {
        int lineCount = 0;
        int countPart = 1;
        List<BookText> bookTextList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (String line: textList) {
            lineCount++;
            stringBuilder.append(line + "\n");
            if (lineCount % 200 == 0) {
                bookTextList.add(new BookText(0, stringBuilder.toString(), countPart, book));
                stringBuilder = new StringBuilder();
                logger.debug("SET PART NUM %d FOR BOOK ID %d".formatted(countPart, book.getId()));
                countPart++;
            }
        }
        if (textList.size() % 200 != 0) {
            bookTextList.add(new BookText(0, stringBuilder.toString(), countPart, book));
        }
        return bookTextList;
    }

    private List<String> splitLongLine(String line) {
        List<String> resultList = new ArrayList<>();
        while (line.length() > COUNT_CHARS_IN_ROWS) {
            int wsPos = getPosLastSpace(line);
            resultList.add(line.substring(0, wsPos).trim());
            line = line.substring(wsPos);
        }
        if (line.length() > 0) {
            resultList.add(line.trim());
        }
        return resultList;
    }

    private int getPosLastSpace(String line) {
        int wsPos = 0;
        wsPos = line.indexOf(" ", COUNT_CHARS_IN_ROWS);
        if (line.length() < COUNT_CHARS_IN_ROWS) {
            return line.length() - 1;
        }
        return wsPos > 0 ? wsPos : COUNT_CHARS_IN_ROWS;
    }
}
