package com.miu.bookhub;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {

    private LocalDateTime scheduledDeliveryTime;
    private LocalDateTime scheduledPickupTime;
    private LocalDateTime actualDeliveryTime;
    private LocalDateTime actualPickupTime;
    private boolean isPremium;
}
