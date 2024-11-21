package ru.otus.hw.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(attributePaths = {"author", "genre"})
    Optional<Book> findById(long id);

    @EntityGraph(attributePaths = {"author", "genre"})
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"author", "genre"})
    Optional<Book> findByTitle(String title);

    @EntityGraph(attributePaths = {"author", "genre"})
    List<Book> findByAuthorFullName(String fullName);

    @EntityGraph(attributePaths = {"author", "genre"})
    Optional<Book> findFirstByAuthorId(long author_id);

    @EntityGraph(attributePaths = {"author", "genre"})
    Optional<Book> findFirstByGenreId(long id);

}
