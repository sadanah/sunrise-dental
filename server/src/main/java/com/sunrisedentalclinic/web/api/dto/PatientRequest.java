package com.sunrisedentalclinic.web.api.dto;
public class PatientRequest {
    private String action, patientID, name, contactNo, address, email;
    public String getAction(){return action;} public void setAction(String v){action=v;}
    public String getPatientID(){return patientID;} public void setPatientID(String v){patientID=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getContactNo(){return contactNo;} public void setContactNo(String v){contactNo=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
}