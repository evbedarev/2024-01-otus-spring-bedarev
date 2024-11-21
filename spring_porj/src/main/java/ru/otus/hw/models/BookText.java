package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "books_text")
public class BookText {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;

    @Column(columnDefinition = "LONGTEXT")
    @Getter
    private String bookText;

    @Getter
    @Column
    private int partNumber;

    @Getter
    @Column
    private int maxPartNumber;

    @Getter
    @Setter
    @Column
    private int maxPage;

    @Getter
    @ManyToOne(targetEntity = Book.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    public BookText(long id, String bookText, int partNumber, Book book) {
        this.id = id;
        this.bookText = bookText;
        this.partNumber = partNumber;
        this.book = book;
    }

}
