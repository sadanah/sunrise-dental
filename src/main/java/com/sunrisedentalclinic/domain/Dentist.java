package com.sunrisedentalclinic.domain;

import java.math.BigDecimal;

public class Dentist extends Staff {
    private String specialization;
    private BigDecimal consultationFee;

    public Dentist(int personID, String name, String contactNo, String address,
                   String staffID, String username, String passwordHash,
                   String specialization, BigDecimal consultationFee) {
        super(personID, name, contactNo, address, staffID, username, passwordHash);
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    @Override
    public String getRole() {
        return "DENTIST";
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    // getSchedule(date) later in service layer
}
