package com.sunrisedentalclinic.service.impl;

import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.domain.Bill;
import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.service.IAppointmentService;
import com.sunrisedentalclinic.service.IAuthService;
import com.sunrisedentalclinic.service.IBillingService;

import java.time.LocalDate;
import java.time.LocalTime;

public class ClinicFacade {

    private final IAppointmentService appointmentService;
    private final IBillingService billingService;
    private final IAuthService authService;

    public ClinicFacade(IAppointmentService appointmentService, IBillingService billingService, IAuthService authService) {
        this.appointmentService = appointmentService;
        this.billingService = billingService;
        this.authService = authService;
    }

    public Session login(String username, String password) {
        return authService.login(username, password);
    }

    public Appointment registerAppointment(String patientID, String dentistID, String treatmentID,
                                           String staffID, LocalDate date, LocalTime time) {
        return appointmentService.registerAppointment(patientID, dentistID, treatmentID, staffID, date, time);
    }

    public void cancelAppointment(String appointmentNo) {
        appointmentService.cancelAppointment(appointmentNo);
    }

    public Bill generateBill(String appointmentNo) {
        return billingService.calculateBill(appointmentNo);
    }

    public Appointment searchAppointment(String appointmentNo) {
        return appointmentService.searchAppointment(appointmentNo);
    }
}