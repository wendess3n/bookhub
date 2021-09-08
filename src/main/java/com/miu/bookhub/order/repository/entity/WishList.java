package com.miu.bookhub.order.repository.entity;

import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.inventory.repository.entity.Book;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "wish_list")
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
