package ru.otus.hw.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.book.TextBookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookText;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookTextRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BookTextServiceImpl implements BookTextService {

    private static final Logger logger = LoggerFactory.getLogger(BookTextServiceImpl.class);

    private static final int COUNT_CHARS_IN_ROWS = 120;

    private final BookTextRepository bookTextRepository;

    private final BookRepository bookRepository;

    @Override
    public void insertText(List<String> lines, int partNumber, long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new EntityNotFoundException("Book with id %s not found".formatted(bookId));
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            if (line.length() > COUNT_CHARS_IN_ROWS) {
                stringBuilder.append(splitLongLine(line));
            } else {
                stringBuilder.append(line + "\n");
            }
        }
        logger.debug("====== INSERT PAGE PART NUBER %s =======".formatted(partNumber));
        logger.debug(stringBuilder.toString());
        logger.debug("====== END PAGE =======");
        if (!stringBuilder.toString().trim().isEmpty()) {
            BookText bookText = new BookText(0, stringBuilder.toString(), partNumber, book.get());
            int maxPage = getCountPages(bookText);
            bookText.setMaxPage(maxPage);
            logger.debug("Set count pages: " + maxPage);
            logger.info("Insert text for bookId=%d, partNumber=%d, countPages=%d"
                    .formatted(bookId, partNumber, maxPage));
            bookTextRepository.save(bookText);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeBookText(long bookId) {
        if (bookTextRepository.existsByBookId(bookId)) {
            bookTextRepository.deleteByBookId(bookId);
            logger.info("delete book_text for bookId=%d ".formatted(bookId));
        }
    }

    @Override
    @Transactional
    public void setPagesOnBook(long bookId) {
        int maxPart =  bookTextRepository.findMaxPartNumberByBookId(bookId);
        bookTextRepository.updateMaxPartNumByBookId(bookId, maxPart);
        logger.info("Update book_text bookId=%d, set maxPartNumber=%d "
                .formatted(bookId, maxPart));
        for (int part = 1; part <= maxPart; part++) {
            setMinPagesOnText(bookId, part);
        }
    }

    @Override
    public TextBookDto findByBookId(long bookId) {
        BookText bookText = bookTextRepository.findById(bookId);
        return TextBookDto.toDto(bookText);
    }

    @Override
    public TextBookDto findByBookIdAndPartNumber(long bookId, int partNumber) {
        BookText bookText = bookTextRepository.findByBookIdAndPartNumber(bookId, partNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with partNumber %s not found".formatted(partNumber)));
        return TextBookDto.toDto(bookText);
    }

    private StringBuilder splitLongLine(String line) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean exitOnSplitLine = false;
        while (line.length() > COUNT_CHARS_IN_ROWS) {
            int wsPos = 0;
            wsPos = line.indexOf(" ", COUNT_CHARS_IN_ROWS);
            if (wsPos > 0) {
                stringBuilder.append(line.substring(0, wsPos) + "\n");
                line = line.substring(wsPos);
                exitOnSplitLine = true;
            } else {
                stringBuilder.append(line + "\n");
                exitOnSplitLine = false;
                break;
            }
        }
        if (exitOnSplitLine)  {
            stringBuilder.append(line + "\n");
        }
        return stringBuilder;
    }

    @Transactional
    private void setMinPagesOnText(long bookId, int partNumber) {
        logger.debug("setMinPagesOnText with params bookid: %d, partNum: %d".formatted(bookId,partNumber));
        int prevPartMaxPage = bookTextRepository.getMaxPageByBookIdAndPartNum(bookId, partNumber);
        int prevPartMinPage = bookTextRepository.getMinPageByBookIdAndPartNum(bookId,partNumber);
        bookTextRepository.updateMinPageByBookIdAndPartNum(bookId, partNumber,
                (prevPartMinPage + prevPartMaxPage) + 1);
    }

    private int getCountPages(BookText bookText) {
        List<String> splitedPages = Arrays.asList(bookText.getBookText().split("\n"));
        return  ((splitedPages.size()/50) + (splitedPages.size() > 201 ? 1 : 0));
    }
}
