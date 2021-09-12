package com.miu.bookhub.order.api;

import com.miu.bookhub.global.utils.SecurityUtils;
import com.miu.bookhub.order.api.domain.OrderRequest;
import com.miu.bookhub.order.api.domain.OrderResponse;
import com.miu.bookhub.order.service.OrderResult;
import com.miu.bookhub.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/orders")
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse orderBook(@RequestBody OrderRequest request) {

        OrderResult order = orderService.orderBook(SecurityUtils.getCurrentUserId(),
                request.getOrderItems(), request.getShippingAddressId(), request.getRemarks());

        return buildOrderResponse(order);
    }

    @PostMapping("/{referenceId}/cancel")
    public void cancelOrder(@PathVariable String referenceId) {

        orderService.cancelOrder(referenceId);
    }

    @GetMapping("/{referenceId}")
    public OrderResponse getBookOrder(@PathVariable String referenceId) {

        return orderService.findOrderByReference(referenceId)
                .map(this::buildOrderResponse)
                .orElse(null);
    }

    @GetMapping
    public List<OrderResponse> getOrders(Pageable pageable) {

        List<OrderResult> orders = orderService.findOrders(SecurityUtils.getCurrentUser(), Optional.ofNullable(pageable).orElse(Pageable.unpaged()));
        return orders.stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse buildOrderResponse(OrderResult orderResult) {

        return OrderResponse.builder()
                .referenceId(orderResult.getReferenceId())
                .price(orderResult.getPrice())
                .totalPrice(orderResult.getTotalPrice())
                .fees(orderResult.getFees())
                .customerId(orderResult.getCustomer().getId())
                .shippingAddressId(orderResult.getShippingAddress().getId())
                .orderDate(orderResult.getOrderDate())
                .paymentStatus(orderResult.getPaymentStatus())
                .shippingStatus(orderResult.getShippingStatus())
                .build();
    }
}
