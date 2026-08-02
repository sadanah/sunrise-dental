package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.domain.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IAppointmentService {
    Appointment registerAppointment(String patientID, String dentistID, String treatmentID,
                                    String staffID, LocalDate date, LocalTime time);
    void cancelAppointment(String appointmentNo);
    Appointment searchAppointment(String appointmentNo);
    void updateStatus(String appointmentNo, String status);
    boolean checkAvailability(String dentistID, LocalDate date, LocalTime time);
    List<Appointment> getUpcomingAppointmentsForDentist(String dentistID);
}