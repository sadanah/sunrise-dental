package com.sunrisedentalclinic.service.impl;

import com.sunrisedentalclinic.dao.PatientDAO;
import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.domain.Bill;
import com.sunrisedentalclinic.domain.Patient;
import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.exception.PatientAlreadyExistsException;
import com.sunrisedentalclinic.exception.PatientNotFoundException;
import com.sunrisedentalclinic.service.IAppointmentService;
import com.sunrisedentalclinic.service.IAuthService;
import com.sunrisedentalclinic.service.IBillingService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ClinicFacade {

    private final IAppointmentService appointmentService;
    private final IBillingService billingService;
    private final IAuthService authService;
    private final PatientDAO patientDAO;

    public ClinicFacade(IAppointmentService appointmentService, IBillingService billingService,
                        IAuthService authService, PatientDAO patientDAO) {
        this.appointmentService = appointmentService;
        this.billingService = billingService;
        this.authService = authService;
        this.patientDAO = patientDAO;
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

    // ===== Patient management (M5 extension — ClinicFacade originally scoped to
    // Auth/Appointment/Billing per M3; extended here since Patient CRUD is a
    // Receptionist-workflow concern and Receptionist should keep one entry point) =====

    public Patient registerPatient(String patientID, String name, String contactNo, String address) {
        if (patientDAO.findById(patientID) != null) {
            throw new PatientAlreadyExistsException("A patient with ID " + patientID + " already exists.");
        }
        Patient patient = new Patient(0, name, contactNo, address, patientID, LocalDate.now());
        patientDAO.save(patient);
        return patient;
    }

    public Patient searchPatient(String patientID) {
        Patient patient = patientDAO.findById(patientID);
        if (patient == null) {
            throw new PatientNotFoundException("No patient found with ID: " + patientID);
        }
        return patient;
    }

    public void deletePatient(String patientID) {
        patientDAO.delete(patientID);
    }

    public List<Patient> listPatients() {
        return patientDAO.findAll();
    }
}