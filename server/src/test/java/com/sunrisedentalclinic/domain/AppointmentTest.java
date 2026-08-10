package com.sunrisedentalclinic.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        appointment = new Appointment("APT001", LocalDate.now().plusDays(1), LocalTime.of(10, 0));
    }

    @Test
    void newAppointment_defaultsToScheduled() {
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    @Test
    void updateStatus_canTransitionFromScheduledToCompleted() {
        appointment.updateStatus(AppointmentStatus.COMPLETED);
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
    }

    @Test
    void updateStatus_throwsExceptionWhenAlreadyCancelled() {
        appointment.cancelAppointment();
        assertThrows(IllegalStateException.class,
                () -> appointment.updateStatus(AppointmentStatus.COMPLETED));
    }

    @Test
    void cancelAppointment_setsStatusToCancelled() {
        appointment.cancelAppointment();
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
    }
}
