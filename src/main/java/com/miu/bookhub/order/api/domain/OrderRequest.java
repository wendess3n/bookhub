package com.miu.bookhub.order.api.domain;

import com.miu.bookhub.order.service.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderRequest {

    private long shippingAddressId;
    private List<OrderItemDto> orderItems;
    private String remarks;
}
