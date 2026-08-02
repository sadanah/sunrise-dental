package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.domain.Appointment;
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

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(emailGateway, smsGateway);
    }

    @Test
    void update_triggersBothEmailAndSms() {
        Appointment appointment = new Appointment("APT001", LocalDate.now(), LocalTime.of(10, 0));
        appointment.setPatientID("P001");

        notificationService.update(appointment);

        verify(emailGateway, times(1)).sendEmail(eq("P001"), anyString());
        verify(smsGateway, times(1)).sendSms(eq("P001"), anyString());
    }
}