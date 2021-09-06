package com.miu.bookhub.inventory.repository.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Enumerated(EnumType.STRING)
    private Condition condition;
    private Integer quantity;

    @Column(name = "unit_price")
    private Double unitPrice;
}
