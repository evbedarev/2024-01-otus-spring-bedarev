package ru.otus.hw.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.otus.hw.models.Book;

import java.util.List;

@AllArgsConstructor
public class BookPagesDto {

    @Getter
    @Setter
    private List<Book> page;

    @Getter
    @Setter
    private int maxCountPages;
}
