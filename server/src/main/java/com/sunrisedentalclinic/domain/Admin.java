package com.sunrisedentalclinic.domain;

public class Admin extends Staff {
    public Admin(int personID, String name, String contactNo, String address,
                 String staffID, String username, String passwordHash) {
        super(personID, name, contactNo, address, staffID, username, passwordHash);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
