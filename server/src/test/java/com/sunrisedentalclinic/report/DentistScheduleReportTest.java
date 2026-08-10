package com.sunrisedentalclinic.report;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.domain.Appointment;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DentistScheduleReportTest {

    @Test
    void generate_returnsAppointmentsForDentistOnDate() {
        AppointmentDAO mockDao = mock(AppointmentDAO.class);
        LocalDate date = LocalDate.of(2026, 8, 1);
        Appointment appt = new Appointment("APT020", date, LocalTime.of(10, 0));
        when(mockDao.findByDentistAndDate("D001", date)).thenReturn(List.of(appt));

        DentistScheduleReport report = new DentistScheduleReport(
                "RPT020", "A001", "D001", date, date, mockDao);
        report.generate();

        assertEquals(1, report.getAppointments().size());
        verify(mockDao).findByDentistAndDate("D001", date);
    }

    @Test
    void generate_dentistWithNoAppointmentsOnDate_returnsEmptyList() {
        AppointmentDAO mockDao = mock(AppointmentDAO.class);
        LocalDate date = LocalDate.of(2026, 12, 25);
        when(mockDao.findByDentistAndDate("D999", date)).thenReturn(List.of());

        DentistScheduleReport report = new DentistScheduleReport(
                "RPT021", "A001", "D999", date, date, mockDao);
        report.generate();

        assertTrue(report.getAppointments().isEmpty());
    }
}