package com.sunrisedentalclinic.domain;

import java.time.LocalDateTime;

public class Session {
    private String sessionID;
    private LocalDateTime loginTime;
    private LocalDateTime expiryTime;
    private String staffID;
    private String role;

    public Session(String sessionID, LocalDateTime loginTime, LocalDateTime expiryTime) {
        this.sessionID = sessionID;
        this.loginTime = loginTime;
        this.expiryTime = expiryTime;
    }

    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiryTime);
    }

    public String getSessionID() { return sessionID; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public LocalDateTime getExpiryTime() { return expiryTime; }
    public String getStaffID() { return staffID; }
    public void setStaffID(String staffID) { this.staffID = staffID; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}