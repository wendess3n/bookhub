package com.miu.bookhub.inventory.repository.entity;

import com.miu.bookhub.order.repository.entity.WishList;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
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

    @Column(name = "cover_uri")
    private String coverUri;

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

    @OneToMany(mappedBy = "book")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "wish_list_id")
    private List<WishList> wishLists;

    @Enumerated(EnumType.STRING)
    private Genre genre;
}
