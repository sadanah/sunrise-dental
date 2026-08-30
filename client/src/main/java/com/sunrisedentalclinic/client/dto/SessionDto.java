package com.sunrisedentalclinic.client.dto;
public class SessionDto {
    private String sessionID, staffID, role, loginTime, expiryTime, name;
    private boolean valid;
    public String getSessionID(){return sessionID;} public void setSessionID(String v){sessionID=v;}
    public String getStaffID(){return staffID;} public void setStaffID(String v){staffID=v;}
    public String getRole(){return role;} public void setRole(String v){role=v;}
    public String getLoginTime(){return loginTime;} public void setLoginTime(String v){loginTime=v;}
    public String getExpiryTime(){return expiryTime;} public void setExpiryTime(String v){expiryTime=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public boolean isValid(){return valid;} public void setValid(boolean v){valid=v;}
}