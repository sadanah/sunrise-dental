package com.sunrisedentalclinic.report;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.domain.Appointment;

import java.time.LocalDate;
import java.util.List;

public class DentistScheduleReport extends Report {

    private final String dentistID;
    private final LocalDate date;
    private final AppointmentDAO appointmentDAO;
    private List<Appointment> appointments;

    public DentistScheduleReport(
            String reportID,
            String generatedBy,
            String dentistID,
            LocalDate date,
            AppointmentDAO appointmentDAO) {

        super(reportID, generatedBy);
        this.dentistID = dentistID;
        this.date = date;
        this.appointmentDAO = appointmentDAO;
    }

    @Override
    public void generate() {
        appointments = appointmentDAO.findByDentistAndDate(dentistID, date);
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}