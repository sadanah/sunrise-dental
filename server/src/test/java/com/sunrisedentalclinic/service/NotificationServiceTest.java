package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dao.PatientDAO;
import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.domain.Patient;
import com.sunrisedentalclinic.service.impl.EmailGateway;
import com.sunrisedentalclinic.service.impl.NotificationService;
import com.sunrisedentalclinic.service.impl.SmsGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private EmailGateway emailGateway;
    @Mock private SmsGateway smsGateway;
    @Mock private PatientDAO patientDAO;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(emailGateway, smsGateway, patientDAO);
    }

    @Test
    void update_looksUpPatientEmail_andTriggersBothEmailAndSms() {
        Appointment appointment = new Appointment("APT001", LocalDate.now(), LocalTime.of(10, 0));
        appointment.setPatientID("P001");

        Patient patient = new Patient(1, "Jane Doe", "0771234567", "Colombo", "P001", LocalDate.now());
        patient.setEmail("jane@example.com");
        when(patientDAO.findById("P001")).thenReturn(patient);

        notificationService.update(appointment);

        verify(emailGateway, times(1)).sendEmail(eq("jane@example.com"), anyString());
        verify(smsGateway, times(1)).sendSms(eq("P001"), anyString());
    }

    @Test
    void update_patientHasNoEmail_stillCallsGatewayWithNull() {
        Appointment appointment = new Appointment("APT001", LocalDate.now(), LocalTime.of(10, 0));
        appointment.setPatientID("P002");

        Patient patient = new Patient(2, "No Email Guy", "0779998887", "Kandy", "P002", LocalDate.now());
        // email left null
        when(patientDAO.findById("P002")).thenReturn(patient);

        notificationService.update(appointment);

        verify(emailGateway, times(1)).sendEmail(isNull(), anyString());
    }

    @Test
    void update_patientNotFound_doesNotThrow() {
        Appointment appointment = new Appointment("APT001", LocalDate.now(), LocalTime.of(10, 0));
        appointment.setPatientID("P999");
        when(patientDAO.findById("P999")).thenReturn(null);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> notificationService.update(appointment));

        verify(emailGateway, times(1)).sendEmail(isNull(), anyString());
    }
}