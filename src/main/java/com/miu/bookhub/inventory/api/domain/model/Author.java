package com.miu.bookhub.inventory.api.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Author {

    private String name;
    private String isni;
    private String photoUri;
}
