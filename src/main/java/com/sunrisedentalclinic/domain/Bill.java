package com.sunrisedentalclinic.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private final String billID;
    private final BigDecimal consultationFee;
    private final BigDecimal treatmentCost;
    private BigDecimal totalAmount;
    private final LocalDateTime generatedDate;
    private String appointmentNo;

    public Bill(String billID, BigDecimal consultationFee, BigDecimal treatmentCost) {
        this.billID = billID;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.generatedDate = LocalDateTime.now();
        this.totalAmount = calculateTotal();
    }

    public BigDecimal calculateTotal() {
        this.totalAmount = consultationFee.add(treatmentCost);
        return this.totalAmount;
    }

    // printReceipt() stub to be implemented in detail at the view level because it is UI related
    public String printReceipt() {
        return String.format("Bill #%s | Consultation: %s | Treatment: %s | Total: %s",
                billID, consultationFee, treatmentCost, totalAmount);
    }

    public String getBillID() { return billID; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public BigDecimal getTreatmentCost() { return treatmentCost; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public String getAppointmentNo() { return appointmentNo; }
    public void setAppointmentNo(String appointmentNo) { this.appointmentNo = appointmentNo; }
}