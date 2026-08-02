package com.sunrisedentalclinic.service.impl;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.domain.AppointmentStatus;
import com.sunrisedentalclinic.exception.AppointmentNotFoundException;
import com.sunrisedentalclinic.exception.InvalidCancellationException;
import com.sunrisedentalclinic.exception.SlotUnavailableException;
import com.sunrisedentalclinic.service.IAppointmentService;
import com.sunrisedentalclinic.service.INotificationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AppointmentService implements IAppointmentService {

    private final AppointmentDAO appointmentDAO;
    private final INotificationService notificationService;

    public AppointmentService(AppointmentDAO appointmentDAO, INotificationService notificationService) {
        this.appointmentDAO = appointmentDAO;
        this.notificationService = notificationService;
    }

    @Override
    public Appointment registerAppointment(String patientID, String dentistID, String treatmentID,
                                           String staffID, LocalDate date, LocalTime time) {
        if (!checkAvailability(dentistID, date, time)) {
            throw new SlotUnavailableException("This dentist already has a scheduled appointment at this date and time");
        }

        String appointmentNo = "APT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Appointment appointment = new Appointment(appointmentNo, date, time);
        appointment.setPatientID(patientID);
        appointment.setDentistID(dentistID);
        appointment.setTreatmentID(treatmentID);
        appointment.setStaffID(staffID);

        appointmentDAO.save(appointment);

        notificationService.update(appointment);

        return appointment;
    }

    @Override
    public void cancelAppointment(String appointmentNo) {
        Appointment appointment = appointmentDAO.findById(appointmentNo);
        if (appointment == null) {
            throw new AppointmentNotFoundException("No appointment found with number: " + appointmentNo);
        }
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new InvalidCancellationException("Cannot cancel an appointment that is already " + appointment.getStatus());
        }

        appointment.cancelAppointment();
        appointmentDAO.update(appointment);
        notificationService.sendCancellationNotice(appointment);
    }

    @Override
    public Appointment searchAppointment(String appointmentNo) {
        Appointment appointment = appointmentDAO.findById(appointmentNo);
        if (appointment == null) {
            throw new AppointmentNotFoundException("No appointment found with number: " + appointmentNo);
        }
        return appointment;
    }

    @Override
    public void updateStatus(String appointmentNo, String status) {
        Appointment appointment = appointmentDAO.findById(appointmentNo);
        if (appointment == null) {
            throw new AppointmentNotFoundException("No appointment found with number: " + appointmentNo);
        }
        appointment.updateStatus(AppointmentStatus.valueOf(status));
        appointmentDAO.update(appointment);
    }

    @Override
    public boolean checkAvailability(String dentistID, LocalDate date, LocalTime time) {
        return appointmentDAO.findByDentistAndDateTime(dentistID, date, time) == null;
    }

    @Override
    public List<Appointment> getUpcomingAppointmentsForDentist(String dentistID) {
        LocalDate today = LocalDate.now();
        return appointmentDAO.findByDentist(dentistID).stream()
                .filter(a -> !a.getAppointmentDate().isBefore(today))
                .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                .collect(Collectors.toList());
    }
}