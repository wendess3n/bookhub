package com.miu.bookhub.order.repository.entity;

import com.miu.bookhub.inventory.repository.entity.BookItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "book_item_id", nullable = false)
    private BookItem bookItem;
}
