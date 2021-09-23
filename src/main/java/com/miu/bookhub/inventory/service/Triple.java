package com.miu.bookhub.inventory.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Triple <L,M,R> {

    private final L left;
    private final M middle;
    private final R right;
}
