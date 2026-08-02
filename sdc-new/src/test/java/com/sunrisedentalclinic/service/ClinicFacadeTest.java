package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dao.PatientDAO;
import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.domain.Bill;
import com.sunrisedentalclinic.domain.Patient;
import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.exception.PatientAlreadyExistsException;
import com.sunrisedentalclinic.exception.PatientNotFoundException;
import com.sunrisedentalclinic.service.impl.ClinicFacade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicFacadeTest {

    @Mock private IAppointmentService appointmentService;
    @Mock private IBillingService billingService;
    @Mock private IAuthService authService;
    @Mock private PatientDAO patientDAO;

    private ClinicFacade clinicFacade;

    @BeforeEach
    void setUp() {
        clinicFacade = new ClinicFacade(appointmentService, billingService, authService, patientDAO);
    }

    @Test
    void login_delegatesToAuthServiceAndReturnsSession() {
        Session expectedSession = new Session("SESS001", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        expectedSession.setStaffID("S001");
        expectedSession.setRole("RECEPTIONIST");

        when(authService.login("user1", "pass1")).thenReturn(expectedSession);

        Session result = clinicFacade.login("user1", "pass1");

        assertSame(expectedSession, result);
        assertEquals("S001", result.getStaffID());
        verify(authService).login("user1", "pass1");
    }

    @Test
    void registerAppointment_delegatesToAppointmentServiceWithCorrectArgs() {
        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalTime time = LocalTime.of(10, 0);

        Appointment expectedAppointment = new Appointment("APT001", date, time);
        expectedAppointment.setPatientID("P001");
        expectedAppointment.setDentistID("D001");
        expectedAppointment.setTreatmentID("T001");
        expectedAppointment.setStaffID("S001");

        when(appointmentService.registerAppointment("P001", "D001", "T001", "S001", date, time))
                .thenReturn(expectedAppointment);

        Appointment result = clinicFacade.registerAppointment("P001", "D001", "T001", "S001", date, time);

        assertSame(expectedAppointment, result);
        assertEquals("APT001", result.getAppointmentNo());
        verify(appointmentService).registerAppointment("P001", "D001", "T001", "S001", date, time);
    }

    @Test
    void cancelAppointment_delegatesToAppointmentServiceWithCorrectArg() {
        clinicFacade.cancelAppointment("APT001");

        verify(appointmentService).cancelAppointment("APT001");
    }

    @Test
    void generateBill_delegatesToBillingServiceAndReturnsBill() {
        Bill expectedBill = new Bill("B001", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        expectedBill.setAppointmentNo("APT001");

        when(billingService.calculateBill("APT001")).thenReturn(expectedBill);

        Bill result = clinicFacade.generateBill("APT001");

        assertSame(expectedBill, result);
        assertEquals(new BigDecimal("6000.00"), result.getTotalAmount());
        verify(billingService).calculateBill("APT001");
    }

    @Test
    void registerPatient_savesAndReturnsPatientWhenIdNotTaken() {
        when(patientDAO.findById("P001")).thenReturn(null);

        Patient result = clinicFacade.registerPatient("P001", "Jane Doe", "0771234567", "123 Main St");

        assertEquals("P001", result.getPatientID());
        assertEquals("Jane Doe", result.getName());
        verify(patientDAO).findById("P001");
        verify(patientDAO).save(any(Patient.class));
    }

    @Test
    void registerPatient_throwsWhenIdAlreadyExists() {
        Patient existing = new Patient(1, "Existing Patient", "0770000000", "Old Address",
                "P001", LocalDate.now());
        when(patientDAO.findById("P001")).thenReturn(existing);

        assertThrows(PatientAlreadyExistsException.class,
                () -> clinicFacade.registerPatient("P001", "Jane Doe", "0771234567", "123 Main St"));

        verify(patientDAO, never()).save(any(Patient.class));
    }

    @Test
    void searchPatient_returnsPatientWhenFound() {
        Patient expected = new Patient(1, "Jane Doe", "0771234567", "123 Main St",
                "P001", LocalDate.of(2026, 1, 1));
        when(patientDAO.findById("P001")).thenReturn(expected);

        Patient result = clinicFacade.searchPatient("P001");

        assertSame(expected, result);
        verify(patientDAO).findById("P001");
    }

    @Test
    void searchPatient_throwsWhenNotFound() {
        when(patientDAO.findById("P999")).thenReturn(null);

        assertThrows(PatientNotFoundException.class,
                () -> clinicFacade.searchPatient("P999"));
    }

    @Test
    void deletePatient_delegatesToPatientDAOWithCorrectArg() {
        clinicFacade.deletePatient("P001");

        verify(patientDAO).delete("P001");
    }

    @Test
    void listPatients_delegatesToPatientDAOAndReturnsList() {
        Patient p1 = new Patient(1, "Jane Doe", "0771234567", "123 Main St", "P001", LocalDate.now());
        Patient p2 = new Patient(2, "John Smith", "0779876543", "456 Oak Ave", "P002", LocalDate.now());
        List<Patient> expected = List.of(p1, p2);
        when(patientDAO.findAll()).thenReturn(expected);

        List<Patient> result = clinicFacade.listPatients();

        assertSame(expected, result);
        assertEquals(2, result.size());
        verify(patientDAO).findAll();
    }
}