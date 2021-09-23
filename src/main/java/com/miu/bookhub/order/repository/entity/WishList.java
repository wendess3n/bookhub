package com.miu.bookhub.order.repository.entity;

import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.inventory.repository.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "wished_date")
    private LocalDate wishedDate;
}
