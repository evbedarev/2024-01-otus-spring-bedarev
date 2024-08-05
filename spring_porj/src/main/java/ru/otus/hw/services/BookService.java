package ru.otus.hw.services;

import org.springframework.data.domain.Page;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> findById(long id);

    List<Book> findAll();

    Page<Book> findAllInPages(int pageNum);

    List<Book> findByAuthorFullName(String fullName);

    Optional<Book> findByTitle(String title);

    Book insert(String title, long authorId, long genreId);

    Book update(long id, String title, long authorId, long genreId);

    void deleteById(long id);
}
