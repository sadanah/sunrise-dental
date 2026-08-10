package com.sunrisedentalclinic.client.dto;
public class AppointmentDto {
    private String appointmentNo, patientID, dentistID, treatmentID, staffID, appointmentDate, appointmentTime, status;
    public String getAppointmentNo(){return appointmentNo;} public void setAppointmentNo(String v){appointmentNo=v;}
    public String getPatientID(){return patientID;} public void setPatientID(String v){patientID=v;}
    public String getDentistID(){return dentistID;} public void setDentistID(String v){dentistID=v;}
    public String getTreatmentID(){return treatmentID;} public void setTreatmentID(String v){treatmentID=v;}
    public String getStaffID(){return staffID;} public void setStaffID(String v){staffID=v;}
    public String getAppointmentDate(){return appointmentDate;} public void setAppointmentDate(String v){appointmentDate=v;}
    public String getAppointmentTime(){return appointmentTime;} public void setAppointmentTime(String v){appointmentTime=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
}