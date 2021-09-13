package com.miu.bookhub.inventory.api.domain;

import com.miu.bookhub.inventory.repository.entity.Condition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BookItemRequest {

    private String isbn;
    private Condition condition;
    private Integer quantity;
    private Double unitPrice;
}
