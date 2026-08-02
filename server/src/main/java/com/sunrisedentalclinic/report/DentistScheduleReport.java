package com.sunrisedentalclinic.report;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.domain.Appointment;

import java.time.LocalDate;
import java.util.List;

public class DentistScheduleReport extends Report {

    private final String dentistID;
    private final AppointmentDAO appointmentDAO;
    private List<Appointment> appointments;

    public DentistScheduleReport(
            String reportID,
            String generatedBy,
            String dentistID,
            LocalDate startDate, AppointmentDAO appointmentDAO) {

        super(reportID, generatedBy);
        this.dentistID = dentistID;
        this.appointmentDAO = appointmentDAO;
    }

    @Override
    public void generate() {
        appointments = appointmentDAO.findByDentist(dentistID);
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}