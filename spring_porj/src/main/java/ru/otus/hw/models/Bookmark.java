package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookmarks")
@AllArgsConstructor
@NoArgsConstructor
public class Bookmark {

    @Id
    @Column
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @Column(name = "username", nullable = false)
    private String username;

    @Getter
    @Column(name = "book_id", nullable = false)
    private long bookId;

    @Getter
    @Column(name = "part_num", nullable = false)
    private int partNum;

    @Getter
    @Column(name = "cur_page", nullable = false)
    private int curPage;

}
