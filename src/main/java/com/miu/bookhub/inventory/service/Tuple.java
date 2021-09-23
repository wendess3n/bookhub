package com.miu.bookhub.inventory.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Tuple<S,U> {

    private final S left;
    private final U right;
}
