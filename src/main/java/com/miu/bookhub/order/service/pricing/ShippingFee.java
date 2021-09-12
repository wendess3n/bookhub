package com.miu.bookhub.order.service.pricing;

import com.miu.bookhub.account.repository.entity.Address;

public class ShippingFee extends Fee {

    private final Address stockAddress;
    private final Address destinationAddress;
    private double rate;

    public ShippingFee(Address stockAddress, Address destinationAddress, double rate) {
        super(FeeType.SHIPPING_FEE);
        this.stockAddress = stockAddress;
        this.destinationAddress = destinationAddress;
    }

    @Override
    public double computeAmount(double baseAmount) {

        //TODO: calculate miles b/n stock and destination address and apply the rate
        return 50; // Flat price for now
    }
}
