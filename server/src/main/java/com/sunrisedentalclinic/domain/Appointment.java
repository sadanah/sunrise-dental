package com.sunrisedentalclinic.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private String appointmentNo;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;
    private AppointmentStatus status;
    private String patientID;
    private String dentistID;
    private String treatmentID;
    private String staffID; // who registered it

    public Appointment(String appointmentNo, LocalDate appointmentDate, LocalTime appointmentTime) {
        this.appointmentNo = appointmentNo;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = AppointmentStatus.SCHEDULED; // default when creating
    }

    public void updateStatus(AppointmentStatus newStatus) {
        // business rule - cant change once it is CANCELLED or COMPLETED
        if (this.status == AppointmentStatus.CANCELLED || this.status == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Cannot change status of an appointment that is already " + this.status);
        }
        this.status = newStatus;
    }

    public void cancelAppointment() {
        updateStatus(AppointmentStatus.CANCELLED);
    }

    public String getAppointmentNo() { return appointmentNo; }
    //setter for appointmentNo
    public void setAppointmentNo(String apt001) {this.appointmentNo = appointmentNo; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public AppointmentStatus getStatus() { return status; }
    public String getPatientID() { return patientID; }
    public void setPatientID(String patientID) { this.patientID = patientID; }
    public String getDentistID() { return dentistID; }
    public void setDentistID(String dentistID) { this.dentistID = dentistID; }
    public String getTreatmentID() { return treatmentID; }
    public void setTreatmentID(String treatmentID) { this.treatmentID = treatmentID; }
    public String getStaffID() { return staffID; }
    public void setStaffID(String staffID) { this.staffID = staffID; }

    // Used only by the DAO layer to reconstruct status from the DB without
    // re-triggering the transition guard logic in updateStatus()
    public void updateStatusFromDB(AppointmentStatus status) {
        this.status = status;
    }
}
