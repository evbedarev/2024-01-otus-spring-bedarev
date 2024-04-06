package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;

    @Column(name = "text", nullable = false, unique = false)
    @Getter
    private String text;

    @ManyToOne(targetEntity = Book.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    @Getter
    private Book book;
}
