package com.sunrisedentalclinic.report;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.dao.BillDAO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ReportFactoryTest {

    private final ReportFactory factory = new ReportFactory(mock(AppointmentDAO.class), mock(BillDAO.class));

    @Test
    void createReport_revenue_returnsRevenueReport() {
        Report report = factory.createReport("REVENUE", "A001", LocalDate.now(), LocalDate.now(), null);
        assertInstanceOf(RevenueReport.class, report);
    }

    @Test
    void createReport_dailyAppointments_returnsDailyAppointmentReport() {
        Report report = factory.createReport("DAILY_APPOINTMENTS", "A001", LocalDate.now(), null, null);
        assertInstanceOf(DailyAppointmentReport.class, report);
    }

    @Test
    void createReport_dentistSchedule_returnsDentistScheduleReport() {
        Report report = factory.createReport("DENTIST_SCHEDULE", "A001", LocalDate.now(), null, "D001");
        assertInstanceOf(DentistScheduleReport.class, report);
    }

    @Test
    void createReport_unknownType_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.createReport("UNKNOWN", "A001", LocalDate.now(), null, null));
    }
}