package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.domain.Bill;
import com.sunrisedentalclinic.domain.Session;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicFacadeTest {

    @Mock private IAppointmentService appointmentService;
    @Mock private IBillingService billingService;
    @Mock private IAuthService authService;

    private ClinicFacade clinicFacade;

    @BeforeEach
    void setUp() {
        clinicFacade = new ClinicFacade(appointmentService, billingService, authService);
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
}