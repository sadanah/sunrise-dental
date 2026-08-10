package com.sunrisedentalclinic.web.api.dto;
public class StaffRequest {
    private String action, role, staffID, name, contactNo, address, username, password, specialization;
    private String consultationFee;
    private String email;
    // getters/setters for all fields
    public String getAction(){return action;} public void setAction(String v){action=v;}
    public String getRole(){return role;} public void setRole(String v){role=v;}
    public String getStaffID(){return staffID;} public void setStaffID(String v){staffID=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getContactNo(){return contactNo;} public void setContactNo(String v){contactNo=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getPassword(){return password;} public void setPassword(String v){password=v;}
    public String getSpecialization(){return specialization;} public void setSpecialization(String v){specialization=v;}
    public String getConsultationFee(){return consultationFee;} public void setConsultationFee(String v){consultationFee=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
}