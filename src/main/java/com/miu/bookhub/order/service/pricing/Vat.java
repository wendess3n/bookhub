package com.miu.bookhub.order.service.pricing;

public class Vat extends Fee{

    private final double rate;

    public Vat(double rate) {
        super(FeeType.VAT);
        this.rate = rate;
    }

    @Override
    public double computeAmount(double baseAmount) {
        return rate * baseAmount;
    }
}
