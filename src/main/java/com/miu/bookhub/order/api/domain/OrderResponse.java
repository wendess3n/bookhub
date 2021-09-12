package com.miu.bookhub.order.api.domain;

import com.miu.bookhub.order.repository.entity.DeliveryStatus;
import com.miu.bookhub.order.repository.entity.PaymentStatus;
import com.miu.bookhub.order.service.FeeDto;
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
public class OrderResponse {

    private String referenceId;
    private Double price;
    private List<FeeDto> fees;
    private Double totalPrice;
    private Long customerId;
    private Long shippingAddressId;
    private LocalDateTime orderDate;
    private PaymentStatus paymentStatus;
    private DeliveryStatus shippingStatus;
}
