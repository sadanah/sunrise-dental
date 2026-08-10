package com.sunrisedentalclinic.report;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.domain.Appointment;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DailyAppointmentReportTest {

    @Test
    void generate_returnsAppointmentsForGivenDate() {
        AppointmentDAO mockDao = mock(AppointmentDAO.class);
        LocalDate date = LocalDate.of(2026, 8, 1);
        Appointment appt = new Appointment("APT001", date, LocalTime.of(9, 0));
        when(mockDao.findByDate(date)).thenReturn(List.of(appt));

        DailyAppointmentReport report = new DailyAppointmentReport("RPT010", "A001", date, mockDao);
        report.generate();

        assertEquals(1, report.getAppointments().size());
        assertEquals("APT001", report.getAppointments().get(0).getAppointmentNo());
        verify(mockDao).findByDate(date);
    }

    @Test
    void generate_noAppointmentsOnDate_returnsEmptyList() {
        AppointmentDAO mockDao = mock(AppointmentDAO.class);
        LocalDate date = LocalDate.of(2026, 12, 25);
        when(mockDao.findByDate(date)).thenReturn(List.of());

        DailyAppointmentReport report = new DailyAppointmentReport("RPT011", "A001", date, mockDao);
        report.generate();

        assertTrue(report.getAppointments().isEmpty());
    }
}