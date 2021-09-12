package com.miu.bookhub.order.service.pricing;

public abstract class Fee {

    public final FeeType type;

    // Package protecting constructor, so that only service can create new fee types
    Fee(FeeType type) {
        this.type = type;
    }

    public abstract double computeAmount(double baseAmount);
}

