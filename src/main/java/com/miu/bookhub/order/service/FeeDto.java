package com.miu.bookhub.order.service;

import com.miu.bookhub.order.service.pricing.FeeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FeeDto {

    private FeeType type;
    private double amount;
}
