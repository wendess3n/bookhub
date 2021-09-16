package com.miu.bookhub;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FuncUtils {

    List<Shipping> getFastShippers(List<Shipping> companies, int year, int k) {

        return companies.stream()
                .map(shipping -> new Pair<>(shipping, getAverageDelay(shipping, year)))
                .sorted(Comparator.comparing(Pair::getRight))
                .limit(k)
                .map(Pair::getLeft)
                .collect(Collectors.toList());
    }

    Double getAverageDelay(Shipping shipping, int year) {

        return shipping.getOrders().stream()
                .filter(order -> order.getScheduledPickupTime().getYear() == year)
                .mapToLong(order ->  getTimeDiff(order.getScheduledDeliveryTime(), order.getActualDeliveryTime())
                        + getTimeDiff(order.getScheduledPickupTime(), order.getActualPickupTime()))
                .average()
                .getAsDouble();
    }

    long getTimeDiff(LocalDateTime from, LocalDateTime to) {
        return ChronoUnit.MINUTES.between(from, to);
    }

    @Data
    @AllArgsConstructor
    static class Pair<U, T> {

        private U left;
        private T right;
    }
}