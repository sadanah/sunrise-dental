package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dao.*;
import com.sunrisedentalclinic.domain.*;
import com.sunrisedentalclinic.service.impl.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock private AppointmentDAO appointmentDAO;
    @Mock private BillDAO billDAO;
    @Mock private DentistDAO dentistDAO;
    @Mock private TreatmentTypeDAO treatmentTypeDAO;

    private BillingService billingService;

    @BeforeEach
    void setUp() {
        billingService = new BillingService(appointmentDAO, billDAO, dentistDAO, treatmentTypeDAO);
    }

    @Test
    void calculateBill_standardStrategy_sumsConsultationAndTreatmentCost() {
        Appointment appointment = new Appointment("APT001", LocalDate.now(), LocalTime.of(10, 0));
        appointment.setDentistID("D001");
        appointment.setTreatmentID("T001");

        Dentist dentist = new Dentist(1, "Dr. Perera", "0771234567", "Colombo",
                "D001", "nperera", "hash", "General", new BigDecimal("2500.00"));
        TreatmentType treatment = new TreatmentType("T001", "Cleaning", new BigDecimal("3500.00"));

        when(appointmentDAO.findById("APT001")).thenReturn(appointment);
        when(dentistDAO.findById("D001")).thenReturn(dentist);
        when(treatmentTypeDAO.findById("T001")).thenReturn(treatment);

        Bill bill = billingService.calculateBill("APT001");

        assertEquals(new BigDecimal("6000.00"), bill.getTotalAmount());
    }

    @Test
    void applyDiscount_reducesTotalByCorrectPercentage() {
        Appointment appointment = new Appointment("APT001", LocalDate.now(), LocalTime.of(10, 0));
        appointment.setAppointmentNo("APT001");
        when(appointmentDAO.findById("APT001")).thenReturn(appointment);

        Bill originalBill = new Bill("B001", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        originalBill.setAppointmentNo("APT001");

        Bill discounted = billingService.applyDiscount(originalBill, new BigDecimal("10")); // 10% off

        assertEquals(new BigDecimal("5400.00"), discounted.getTotalAmount());
    }
}