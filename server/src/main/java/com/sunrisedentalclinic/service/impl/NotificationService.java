package com.sunrisedentalclinic.service.impl;

import com.sunrisedentalclinic.dao.PatientDAO;
import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.domain.Patient;
import com.sunrisedentalclinic.service.INotificationService;

public class NotificationService implements INotificationService {

    private final EmailGateway emailGateway;
    private final SmsGateway smsGateway;
    private final PatientDAO patientDAO;

    public NotificationService(EmailGateway emailGateway, SmsGateway smsGateway, PatientDAO patientDAO) {
        this.emailGateway = emailGateway;
        this.smsGateway = smsGateway;
        this.patientDAO = patientDAO;
    }

    @Override
    public void sendAppointmentReminder(Appointment appointment) {
        String message = "Reminder: You have an appointment on " + appointment.getAppointmentDate()
                + " at " + appointment.getAppointmentTime();
        notify(appointment, message);
    }

    @Override
    public void sendCancellationNotice(Appointment appointment) {
        String message = "Your appointment on " + appointment.getAppointmentDate() + " has been cancelled.";
        notify(appointment, message);
    }

    @Override
    public void update(Appointment appointment) {
        sendAppointmentReminder(appointment);
    }

    private void notify(Appointment appointment, String message) {
        Patient patient = patientDAO.findById(appointment.getPatientID());
        String email = patient != null ? patient.getEmail() : null;

        emailGateway.sendEmail(email, message);
        smsGateway.sendSms(appointment.getPatientID(), message);
    }
}