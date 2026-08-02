package com.sunrisedentalclinic.web.api.dto;

public class RegisterAppointmentRequest {
    private String patientID;
    private String dentistID;
    private String treatmentID;
    private String date;   // ISO format: "2026-08-15"
    private String time;   // "10:00"

    public String getPatientID() { return patientID; }
    public void setPatientID(String patientID) { this.patientID = patientID; }
    public String getDentistID() { return dentistID; }
    public void setDentistID(String dentistID) { this.dentistID = dentistID; }
    public String getTreatmentID() { return treatmentID; }
    public void setTreatmentID(String treatmentID) { this.treatmentID = treatmentID; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}