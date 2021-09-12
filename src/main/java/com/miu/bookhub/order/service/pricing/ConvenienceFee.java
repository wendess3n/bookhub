package com.miu.bookhub.order.service.pricing;

public class ConvenienceFee extends Fee {

    private final double rate;

    public ConvenienceFee(double rate) {
        super(FeeType.CONVENIENCE_FEE);
        this.rate = rate;
    }

    @Override
    public double computeAmount(double baseAmount) {
        return baseAmount * rate;
    }
}
