package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    private String title;

    private String author;

    private String genre;

    private int pages;

    @Override
    public String toString() {
        return "title: %s, author: %s , genre: %s".formatted(title, author, genre);
    }
}
