package com.miu.bookhub.order.service;

import com.miu.bookhub.account.repository.entity.Address;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.order.repository.entity.DeliveryStatus;
import com.miu.bookhub.order.repository.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderResult {

    private String referenceId;
    private double price;
    private List<FeeDto> fees;
    private double totalPrice;
    private User customer;
    private Address shippingAddress;
    private LocalDateTime orderDate;
    private PaymentStatus paymentStatus;
    private DeliveryStatus shippingStatus;
}
