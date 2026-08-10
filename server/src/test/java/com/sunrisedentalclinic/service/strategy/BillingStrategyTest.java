package com.sunrisedentalclinic.service.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BillingStrategyTest {

    @Test
    void standardStrategy_addsConsultationAndTreatmentCost() {
        IBillingStrategy strategy = new StandardBillingStrategy();
        BigDecimal result = strategy.calculate(null, BigDecimal.valueOf(5000), BigDecimal.valueOf(3000));
        assertEquals(0, BigDecimal.valueOf(8000).compareTo(result));
    }

    @Test
    void discountStrategy_appliesPercentageDiscountToTotal() {
        IBillingStrategy strategy = new DiscountBillingStrategy(BigDecimal.valueOf(10));
        BigDecimal result = strategy.calculate(null, BigDecimal.valueOf(5000), BigDecimal.valueOf(5000));
        // total 10000, 10% discount = 1000 off -> 9000
        assertEquals(0, BigDecimal.valueOf(9000).compareTo(result));
    }

    @Test
    void discountStrategy_zeroPercent_equalsFullTotal() {
        IBillingStrategy strategy = new DiscountBillingStrategy(BigDecimal.ZERO);
        BigDecimal result = strategy.calculate(null, BigDecimal.valueOf(5000), BigDecimal.valueOf(3000));
        assertEquals(0, BigDecimal.valueOf(8000).compareTo(result));
    }

    @Test
    void discountStrategy_hundredPercent_zeroesOutTotal() {
        IBillingStrategy strategy = new DiscountBillingStrategy(BigDecimal.valueOf(100));
        BigDecimal result = strategy.calculate(null, BigDecimal.valueOf(5000), BigDecimal.valueOf(3000));
        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }
}