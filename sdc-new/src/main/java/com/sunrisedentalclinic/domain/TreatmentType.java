package com.sunrisedentalclinic.domain;

import java.math.BigDecimal;

public class TreatmentType {
    private String treatmentID;
    private String treatmentName;
    private BigDecimal baseCost;

    public TreatmentType(String treatmentID, String treatmentName, BigDecimal baseCost) {
        this.treatmentID = treatmentID;
        this.treatmentName = treatmentName;
        this.baseCost = baseCost;
    }

    public String getTreatmentID() { return treatmentID; }
    public void setTreatmentID(String treatmentID) { this.treatmentID = treatmentID; }
    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
    public BigDecimal getBaseCost() { return baseCost; }
    public void setBaseCost(BigDecimal baseCost) { this.baseCost = baseCost; }
}