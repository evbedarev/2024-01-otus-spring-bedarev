package ru.otus.hw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookInRead;

import org.apache.commons.lang3.RandomUtils;
import ru.otus.hw.models.FinishedBook;

@Service
public class LibraryServiceImpl implements LibraryService {
    private final Logger logger = LoggerFactory.getLogger("Integration");
    @Override
    public BookInRead issueBook(Book book) {
        logger.info("Reading book {}", book.getTitle());
        BookInRead bookInRead = new BookInRead(book, true , RandomUtils.nextInt(0,200));
        delay();
        return bookInRead;
    }

    @Override
    public FinishedBook finish(BookInRead bookInRead) {
        logger.info("Book with title %s finished to read".formatted(bookInRead.getBook().getTitle()));
        return new FinishedBook(bookInRead.getBook(),
                "Comment id %d".formatted(RandomUtils.nextInt(0,100)));
    }

    private static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
