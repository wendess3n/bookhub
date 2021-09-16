package com.miu.bookhub;

import lombok.Data;

import java.util.List;

@Data
public class Shipping {

    private String name;
    private List<Order> orders;
}
