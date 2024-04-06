package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;

    @Column(name = "name",nullable = false, unique = true)
    @Getter
    private String name;
}
