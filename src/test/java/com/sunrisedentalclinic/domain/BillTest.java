package com.sunrisedentalclinic.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class BillTest {

    @Test
    void calculateTotal_sumsConsultationAndTreatmentCost() {
        Bill bill = new Bill("B001", new BigDecimal("50.00"), new BigDecimal("120.00"));
        assertEquals(new BigDecimal("170.00"), bill.getTotalAmount());
    }
}
