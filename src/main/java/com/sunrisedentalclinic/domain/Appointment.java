package com.sunrisedentalclinic.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private final String appointmentNo;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;
    private AppointmentStatus status;

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
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public AppointmentStatus getStatus() { return status; }
}
