package com.miu.bookhub.inventory.repository.entity;

import com.miu.bookhub.account.repository.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@Table(name = "book_item")
public class BookItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "seller_Id")
    private User seller;

    @Enumerated(EnumType.STRING)
    private Condition condition;
    private Integer quantity;

    @Column(name = "unit_price")
    private Double unitPrice;
}
