package ru.otus.hw.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookInRead;

import java.util.Collection;

@MessagingGateway
public interface LibraryGateway {
    @Gateway(requestChannel = "booksChannel", replyChannel = "issuingBooksChannel")
    Collection<BookInRead> process(Collection<Book> books);

}
