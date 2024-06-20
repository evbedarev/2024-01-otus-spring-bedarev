package ru.otus.hw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookInRead;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Service
public class BooksServiceImpl implements BooksService {

    private final Logger logger = LoggerFactory.getLogger("Integration");

    private final LibraryGateway libraryGateway;

    public BooksServiceImpl(LibraryGateway libraryGateway) {
        this.libraryGateway = libraryGateway;
    }

    @Override
    public void createLibrary() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        for (int i = 0; i < 10; i++) {
            int num = i + 1;
            pool.execute(() -> {
                        Collection<Book> books = getBooks(num);
                        logger.info("{} New books: {} ", num , books.stream().map(Book::getTitle)
                                .collect(Collectors.joining(",")));
                        Collection<BookInRead> bookInReads = libraryGateway.process(books);
                        bookInReads.stream().forEach(c -> logger.info("{} Return Book to Library {} stop on page {} "
                                ,num, c.getBook().getTitle(), c.getCurrentPage()));
                    }
            );
            delay();
        }
    }

    private Collection<Book> getBooks(int num) {
        List<Book> books =  new ArrayList<>();
        for (int i = 1; i < 15; i++) {
            books.add(new Book("Title_%d%d".formatted(num,i),
                    "Author_%d%d".formatted(num,i),
                    "Genre_%d%d".formatted(num,i),
                    100));
        }
        return books;
    }

    private void delay() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
