package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.BookText;

import java.util.Optional;

public interface BookTextRepository  extends JpaRepository<BookText, Long> {

    BookText findById(long bookId);

    Optional<BookText> findByBookIdAndPartNumber(long bookId, int partNumber);

    boolean existsByBookId(long bookId);

    @Modifying
    @Query("delete from BookText b where b.book.id = :bookId")
    void deleteByBookId(long bookId);

    @Modifying
    @Query("update BookText b set b.maxPartNumber = :maxPartNum where b.book.id  = :book_id")
    void updateMaxPartNumByBookId(@Param("book_id") long book_id,
                                  @Param("maxPartNum") int maxPartNum);

    @Query("SELECT MAX(b.partNumber) FROM BookText b where b.book.id = :book_id")
    int findMaxPartNumberByBookId(@Param("book_id") long bookId);
}
