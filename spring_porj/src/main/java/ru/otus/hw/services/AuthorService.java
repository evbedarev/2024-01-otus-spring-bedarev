package ru.otus.hw.services;

import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<Author> findAll();

    Author findById(long id);

    Author findByFullName(String fullName);

    Author insert(String fullName, String aboutAuthor);

    Author update(long id, String fullName, String aboutAuthor);

    void delete(long id);
}
