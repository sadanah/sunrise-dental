package com.sunrisedentalclinic.report;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.domain.Appointment;

import java.time.LocalDate;
import java.util.List;

public class DentistScheduleReport extends Report {

    private final String dentistID;
    private final AppointmentDAO appointmentDAO;
    private List<Appointment> appointments;
    private final LocalDate reportDate;

    public DentistScheduleReport(
            String reportID,
            String generatedBy,
            String dentistID,
            LocalDate reportDate,
            AppointmentDAO appointmentDAO) {

        super(reportID, generatedBy);
        this.dentistID = dentistID;
        this.reportDate = reportDate;
        this.appointmentDAO = appointmentDAO;
    }

    @Override
    public void generate() {
        appointments = appointmentDAO.findByDentistAndDate(
                dentistID,
                reportDate
        );
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}