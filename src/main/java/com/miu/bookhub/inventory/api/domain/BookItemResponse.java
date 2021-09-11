package com.miu.bookhub.inventory.api.domain;

import com.miu.bookhub.inventory.repository.entity.Condition;
import com.miu.bookhub.inventory.repository.entity.Format;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BookItemResponse {

    private Long bookItemId;
    private Long bookId;
    private String isbn;
    private Format format;
    private Condition condition;
    private Integer quantity;
    private Double unitPrice;
}
