package com.sunrisedentalclinic.report;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.dao.BillDAO;

import java.time.LocalDate;
import java.util.UUID;

public class ReportFactory {

    private final AppointmentDAO appointmentDAO;
    private final BillDAO billDAO;

    public ReportFactory(AppointmentDAO appointmentDAO, BillDAO billDAO) {
        this.appointmentDAO = appointmentDAO;
        this.billDAO = billDAO;
    }

    public Report createReport(String type, String generatedBy, LocalDate startDate, LocalDate endDate, String dentistID) {
        String reportID = "RPT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        switch (type) {
            case "REVENUE":
                return new RevenueReport(reportID, generatedBy, startDate, endDate, billDAO);
            case "DAILY_APPOINTMENTS":
                return new DailyAppointmentReport(reportID, generatedBy, startDate, appointmentDAO);
            case "DENTIST_SCHEDULE":
                return new DentistScheduleReport(reportID, generatedBy, dentistID, startDate, appointmentDAO);
            default:
                throw new IllegalArgumentException("Unknown report type: " + type);
        }
    }
}