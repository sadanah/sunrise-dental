package com.sunrisedentalclinic.service.impl;

import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.service.INotificationService;

public class NotificationService implements INotificationService {

    private final EmailGateway emailGateway;
    private final SmsGateway smsGateway;

    public NotificationService(EmailGateway emailGateway, SmsGateway smsGateway) {
        this.emailGateway = emailGateway;
        this.smsGateway = smsGateway;
    }

    @Override
    public void sendAppointmentReminder(Appointment appointment) {
        String message = "Reminder: You have an appointment on " + appointment.getAppointmentDate()
                + " at " + appointment.getAppointmentTime();
        emailGateway.sendEmail(appointment.getPatientID(), message);
        smsGateway.sendSms(appointment.getPatientID(), message);
    }

    @Override
    public void sendCancellationNotice(Appointment appointment) {
        String message = "Your appointment on " + appointment.getAppointmentDate() + " has been cancelled.";
        emailGateway.sendEmail(appointment.getPatientID(), message);
        smsGateway.sendSms(appointment.getPatientID(), message);
    }

    @Override
    public void update(Appointment appointment) {
        sendAppointmentReminder(appointment);
    }
}