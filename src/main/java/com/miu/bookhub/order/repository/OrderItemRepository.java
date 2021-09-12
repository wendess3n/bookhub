package com.miu.bookhub.order.repository;

import com.miu.bookhub.order.repository.entity.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
}
