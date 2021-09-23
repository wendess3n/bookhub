package com.miu.bookhub.inventory.service;

@FunctionalInterface
public interface TriFunction<S,U,V,R> {

    R apply(S s, U u, V v);
}
