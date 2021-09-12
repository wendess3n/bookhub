package com.miu.bookhub.order.service;

import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.order.repository.entity.DeliveryStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderResult orderBook(long customerId, List<OrderItemDto> orderItems, long addressId, String remarks);

    Optional<OrderResult> findOrderByReference(String referenceId);

    List<OrderResult> findOrders(User customer, Pageable pageable);

    void checkoutOrder(String referenceId);

    void cancelOrder(String referenceId);

    void updateOrderDeliveryStatus(String referenceId, DeliveryStatus deliveryStatus);
}
