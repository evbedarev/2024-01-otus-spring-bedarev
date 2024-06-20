package ru.otus.hw.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "books")
public class Book {
    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String title;

    @DBRef
    @Getter
    @Setter
    private Author author;

    @DBRef
    @Getter
    @Setter
    private Genre genre;

    public Book () {
    }

    public Book(String title, Author author, Genre genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public Book(String id, String title, Author author, Genre genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "title: %s, author: %s, authorId: %s".formatted(title, author.getFullName(), author.getId());
    }
}
