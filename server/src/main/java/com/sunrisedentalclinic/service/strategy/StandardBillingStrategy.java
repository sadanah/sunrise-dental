package com.sunrisedentalclinic.service.strategy;

import com.sunrisedentalclinic.domain.Appointment;
import java.math.BigDecimal;

public class StandardBillingStrategy implements IBillingStrategy {
    @Override
    public BigDecimal calculate(Appointment appointment, BigDecimal consultationFee, BigDecimal treatmentCost) {
        return consultationFee.add(treatmentCost);
    }
}