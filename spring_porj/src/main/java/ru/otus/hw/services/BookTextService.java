package ru.otus.hw.services;

import ru.otus.hw.dto.book.TextBookDto;

import java.util.List;

public interface BookTextService {
    void insertText(List<String> lines, int partNumber, long bookId);

    TextBookDto findByBookId(long bookId);

    TextBookDto findByBookIdAndPartNumber(long bookId, int partNumber);

    void setPagesOnBook(long bookId);

    void removeBookText(long bookId);
}
