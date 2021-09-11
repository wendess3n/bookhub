package com.miu.bookhub.inventory.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Author {

    private String name;
    private String isni;
    private String photoUri;
}
