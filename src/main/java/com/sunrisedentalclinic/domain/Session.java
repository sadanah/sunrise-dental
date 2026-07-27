package com.sunrisedentalclinic.domain;

import java.time.LocalDateTime;

public class Session {
    private String sessionID;
    private LocalDateTime loginTime;
    private LocalDateTime expiryTime;

    public Session(String sessionID, LocalDateTime loginTime, LocalDateTime expiryTime) {
        this.sessionID = sessionID;
        this.loginTime = loginTime;
        this.expiryTime = expiryTime;
    }

    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiryTime);
    }

    public String getsessionID() { return sessionID; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public LocalDateTime getExpiryTime() { return expiryTime; }
}