package com.sunrisedentalclinic.service.strategy;

import com.sunrisedentalclinic.domain.Appointment;
import java.math.BigDecimal;

public class DiscountBillingStrategy implements IBillingStrategy {

    private final BigDecimal discountPercent;

    public DiscountBillingStrategy(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Override
    public BigDecimal calculate(Appointment appointment, BigDecimal consultationFee, BigDecimal treatmentCost) {
        BigDecimal total = consultationFee.add(treatmentCost);
        BigDecimal discountAmount = total.multiply(discountPercent).divide(BigDecimal.valueOf(100));
        return total.subtract(discountAmount);
    }
}