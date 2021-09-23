package com.miu.bookhub.inventory.repository.entity;

import com.miu.bookhub.account.repository.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class BookRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rater_id")
    private User rater;

    @JoinColumn(name = "book_id")
    @ManyToOne
    private Book book;
    private Integer score;
    private String comment;

    @Column(name = "rating_date")
    private LocalDate ratingDate;
}
