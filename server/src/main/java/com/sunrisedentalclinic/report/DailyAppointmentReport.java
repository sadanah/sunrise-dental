package com.sunrisedentalclinic.report;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.domain.Appointment;

import java.time.LocalDate;
import java.util.List;

public class DailyAppointmentReport extends Report {

    private final LocalDate date;
    private final AppointmentDAO appointmentDAO;
    private List<Appointment> appointments;

    public DailyAppointmentReport(
            String reportID,
            String generatedBy,
            LocalDate date,
            AppointmentDAO appointmentDAO) {

        super(reportID, generatedBy);
        this.date = date;
        this.appointmentDAO = appointmentDAO;
    }

    @Override
    public void generate() {
        appointments = appointmentDAO.findByDate(date);
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}