package com.sunrisedentalclinic.client.dto;
public class PatientDto {
    private String patientID, name, contactNo, address, registeredDate;
    public String getPatientID(){return patientID;} public void setPatientID(String v){patientID=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getContactNo(){return contactNo;} public void setContactNo(String v){contactNo=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getRegisteredDate(){return registeredDate;} public void setRegisteredDate(String v){registeredDate=v;}
}