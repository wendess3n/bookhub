package com.miu.bookhub.order.service;

import com.miu.bookhub.order.repository.entity.DeliveryStatus;
import com.miu.bookhub.order.repository.entity.OrderItem;
import com.miu.bookhub.order.repository.entity.PaymentStatus;

import java.util.List;

public interface OrderService {

    void addBookToWishList(long customerId, long bookId);

    void orderBook(long customerId, List<OrderItem> orderItems, long addressId);

    void checkoutOrder(long orderId);

    void updateOrderPaymentStatus(long orderId, PaymentStatus paymentStatus);

    void updateOrderDeliveryStatus(long orderId, DeliveryStatus deliveryStatus);
}
