package ru.otus.hw.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "authors")
public class Author {

    @Id
    private String id;

    private String fullName;

    public Author() {
    }

    public Author(String fullName) {
        this.fullName = fullName;
    }

    public Author(String id, String fullName) {
        this.fullName = fullName;
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Author id: %s, fullName: %s".formatted(getId(),getFullName());
    }
}
