package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.domain.Appointment;

public interface INotificationService {
    void sendAppointmentReminder(Appointment appointment);
    void sendCancellationNotice(Appointment appointment);
    void update(Appointment appointment);
}