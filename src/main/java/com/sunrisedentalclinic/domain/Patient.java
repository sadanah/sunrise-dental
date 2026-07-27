package com.sunrisedentalclinic.domain;

import java.time.LocalDate;

public class Patient extends Person {
    private String patientID;
    private LocalDate registeredDate;

    public Patient(int personId, String name, String contactNo, String address,
                   String patientID, LocalDate registeredDate) {
        super(personId, name, contactNo, address);
        this.patientID = patientID;
        this.registeredDate = registeredDate;
    }

    @Override
    public String getDetails() {
        return "Patient: " + name + " (ID: " + patientID + ")";
    }

    public String getPatientID() { return patientID; }
    public void setPatientID(String patientID) { this.patientID = patientID; }
    public LocalDate getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(LocalDate registeredDate) { this.registeredDate = registeredDate; }

    // create GetAppointmentHistory later
}
