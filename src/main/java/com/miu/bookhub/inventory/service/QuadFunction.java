package com.miu.bookhub.inventory.service;

@FunctionalInterface
public interface QuadFunction<S,U,V,R,T> {

    T apply(S s, U u, V v, R r);
}
