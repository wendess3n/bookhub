package com.miu.bookhub.inventory.repository.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;
    private String edition;
    private String publisher;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @Column(name = "paga_count")
    private Integer pageCount;

    private Double weight;

    @Enumerated(EnumType.STRING)
    private Format format;

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Column(nullable = false)
    private List<Author> authors;
}
