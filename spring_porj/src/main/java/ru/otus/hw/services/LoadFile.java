package ru.otus.hw.services;

import ru.otus.hw.models.BookText;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public interface LoadFile {
    List<BookText> load(Scanner scanner, long book_id);

}
