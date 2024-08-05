package ru.otus.hw.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Bookmark;

import java.util.Optional;

public interface BookmarksRepository extends JpaRepository<Bookmark, Long> {

    @Modifying
    @Query("DELETE FROM Bookmark b where b.bookId = :bookId and b.username = :username")
    void deleteByBookIdAndUsername(@Param("bookId") long bookId, @Param("username") String username);

    @Query("SELECT b FROM Bookmark b where b.bookId = :bookId and b.username = :username")
    Optional<Bookmark> findByBookIdAndUsername(@Param("bookId") long bookId, @Param("username") String username);

    boolean existsByBookId(long bookId);

    void deleteByBookId(long bookId);
}
