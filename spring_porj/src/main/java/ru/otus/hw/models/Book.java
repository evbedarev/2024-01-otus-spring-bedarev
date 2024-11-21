package ru.otus.hw.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")

public class Book {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;

    @Column(name = "title", nullable = true, unique = false)
    @Getter
    private String title;

    @ManyToOne(targetEntity = Author.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @Getter
    private Author author;

    @ManyToOne(targetEntity = Genre.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", unique = false, nullable = true)
    @Getter
    private Genre genre;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book bookObj = (Book) obj;
        StringBuilder thisStringBuilder = new StringBuilder();
        StringBuilder extStringBuilder = new StringBuilder();
        thisStringBuilder.append(id).append(title).append(author.getId())
                .append(author.getFullName()).append(author.getAboutAuthor())
                .append(genre.getId()).append(genre.getName());
        extStringBuilder.append(bookObj.id).append(bookObj.title).append(bookObj.author.getId())
                .append(bookObj.author.getFullName()).append(bookObj.author.getAboutAuthor())
                .append(bookObj.genre.getId()).append(bookObj.genre.getName());
        return  extStringBuilder.equals(thisStringBuilder);
    }

}
