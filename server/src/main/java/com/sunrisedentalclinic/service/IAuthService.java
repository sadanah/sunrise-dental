package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.domain.Staff;

public interface IAuthService {
    Session login(String username, String password);
    void logout(String sessionID);
    boolean validateSession(String sessionID);
    Staff getCurrentUser(String sessionID);
}