package com.miu.bookhub;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {

    private LocalDateTime scheduledDeliveryTime;
    private LocalDateTime scheduledPickupTime;
    private LocalDateTime actualDeliveryTime;
    private LocalDateTime actualPickupTime;
    private boolean isPremium;
    private List<OrderItem> items;
}
