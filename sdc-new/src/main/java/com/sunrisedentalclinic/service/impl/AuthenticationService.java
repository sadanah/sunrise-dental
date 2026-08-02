package com.sunrisedentalclinic.service.impl;

import com.sunrisedentalclinic.dao.StaffDAO;
import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.domain.Staff;
import com.sunrisedentalclinic.exception.AuthenticationException;
import com.sunrisedentalclinic.service.IAuthService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthenticationService implements IAuthService {

    private final StaffDAO staffDAO;
    private final Map<String, Session> activeSessions = new HashMap<>();

    public AuthenticationService(StaffDAO staffDAO) {
        this.staffDAO = staffDAO;
    }

    @Override
    public Session login(String username, String password) {
        Staff staff = staffDAO.findByUsername(username);
        if (staff == null) {
            throw new AuthenticationException("Invalid username or password");
        }

        String hashedInput = hashPassword(password);
        if (!hashedInput.equals(staff.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }

        Session session = new Session(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30)
        );
        session.setStaffID(staff.getStaffID());
        session.setRole(staff.getRole());

        activeSessions.put(session.getSessionID(), session);
        return session;
    }

    @Override
    public void logout(String sessionID) {
        activeSessions.remove(sessionID);
    }

    @Override
    public boolean validateSession(String sessionID) {
        Session session = activeSessions.get(sessionID);
        return session != null && session.isValid();
    }

    @Override
    public Staff getCurrentUser(String sessionID) {
        Session session = activeSessions.get(sessionID);
        if (session == null || !session.isValid()) {
            throw new AuthenticationException("Session invalid or expired");
        }
        return staffDAO.findById(session.getStaffID());
    }

    private String hashPassword(String password) {
        return com.sunrisedentalclinic.util.PasswordUtil.hash(password);
    }
}