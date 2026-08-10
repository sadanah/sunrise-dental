package com.sunrisedentalclinic.service.strategy;

import com.sunrisedentalclinic.domain.Appointment;
import java.math.BigDecimal;

public interface IBillingStrategy {
    BigDecimal calculate(Appointment appointment, BigDecimal consultationFee, BigDecimal treatmentCost);
}