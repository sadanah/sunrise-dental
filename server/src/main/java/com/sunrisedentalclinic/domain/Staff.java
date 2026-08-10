package com.sunrisedentalclinic.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Staff extends Person {
    private String staffID;
    private String username;
    private String passwordHash;

    public Staff(int personID, String name, String contactNo, String address,
                 String staffID, String username, String passwordHash) {
        super(personID, name, contactNo, address);
        this.staffID = staffID;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public abstract String getRole();

    @Override
    public String getDetails() {
        return getRole() + ": " + name + " (Staff ID: " + staffID + ")";
    }

    public String getStaffID() { return staffID; }
    public void setStaffID(String staffID) { this.staffID = staffID; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @JsonIgnore
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}