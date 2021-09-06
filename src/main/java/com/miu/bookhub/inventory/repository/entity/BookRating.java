package com.miu.bookhub.inventory.repository.entity;

import com.miu.bookhub.account.repository.entity.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class BookRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rater_id")
    private User rater;
    private Integer score;
    private String comment;
}
