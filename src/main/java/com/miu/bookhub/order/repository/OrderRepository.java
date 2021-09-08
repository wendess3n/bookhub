package com.miu.bookhub.order.repository;

import com.miu.bookhub.order.repository.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
