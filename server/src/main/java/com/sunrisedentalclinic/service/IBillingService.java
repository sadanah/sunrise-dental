package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.domain.Bill;

public interface IBillingService {
    Bill calculateBill(String appointmentNo);
    Bill applyDiscount(Bill bill, java.math.BigDecimal discountPercent);
    void printReceipt(String billID);
    Bill getBillById(String billID);
}