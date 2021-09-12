package com.miu.bookhub.order.repository;

import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.order.repository.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> findOrderByReferenceId(String referenceId);

    List<Order> findAllByCustomerOrderByOrderDateDesc(User customer, Pageable pageable);
}
