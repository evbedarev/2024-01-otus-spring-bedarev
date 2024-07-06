package ru.otus.hw.service;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookInRead;
import ru.otus.hw.models.FinishedBook;

public interface LibraryService {

    BookInRead issueBook(Book book);

    FinishedBook finish(BookInRead bookInRead);
}
