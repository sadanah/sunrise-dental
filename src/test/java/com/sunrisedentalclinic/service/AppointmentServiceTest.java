package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.exception.SlotUnavailableException;
import com.sunrisedentalclinic.service.impl.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentDAO appointmentDAO;

    @Mock
    private INotificationService notificationService;

    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(appointmentDAO, notificationService);
    }

    @Test
    void registerAppointment_throwsExceptionWhenSlotAlreadyTaken() {
        LocalDate date = LocalDate.now().plusDays(3);
        LocalTime time = LocalTime.of(10, 0);

        // This is the TDD step: write this assertion BEFORE the availability check exists
        when(appointmentDAO.findByDentistAndDateTime("D001", date, time))
                .thenReturn(new Appointment("APT999", date, time)); // simulate existing appointment

        assertThrows(SlotUnavailableException.class, () ->
                appointmentService.registerAppointment("P001", "D001", "T001", "R001", date, time));

        verify(appointmentDAO, never()).save(any());
    }

    @Test
    void registerAppointment_succeedsWhenSlotIsFree() {
        LocalDate date = LocalDate.now().plusDays(3);
        LocalTime time = LocalTime.of(14, 0);

        when(appointmentDAO.findByDentistAndDateTime("D001", date, time)).thenReturn(null);

        Appointment result = appointmentService.registerAppointment("P001", "D001", "T001", "R001", date, time);

        assertNotNull(result);
        verify(appointmentDAO, times(1)).save(any(Appointment.class));
        verify(notificationService, times(1)).update(any(Appointment.class));
    }
}