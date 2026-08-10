package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dao.StaffDAO;
import com.sunrisedentalclinic.domain.Receptionist;
import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.exception.AuthenticationException;
import com.sunrisedentalclinic.service.impl.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private StaffDAO staffDAO;

    private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService(staffDAO);
    }

    @Test
    void login_returnsSessionWhenCredentialsCorrect() throws Exception {
        String correctHash = sha256("password123");
        Receptionist mockStaff = new Receptionist(1, "Kasun Silva", "0779876543", "Colombo",
                "R001", "ksilva", correctHash);

        when(staffDAO.findByUsername("ksilva")).thenReturn(mockStaff);

        Session session = authService.login("ksilva", "password123");

        assertNotNull(session);
        assertTrue(session.isValid());
        assertEquals("R001", session.getStaffID());
    }

    @Test
    void login_throwsExceptionWhenUserNotFound() {
        when(staffDAO.findByUsername("unknown")).thenReturn(null);

        assertThrows(AuthenticationException.class,
                () -> authService.login("unknown", "anything"));
    }

    @Test
    void login_throwsExceptionWhenPasswordIncorrect() throws Exception {
        String correctHash = sha256("password123");
        Receptionist mockStaff = new Receptionist(1, "Kasun Silva", "0779876543", "Colombo",
                "R001", "ksilva", correctHash);

        when(staffDAO.findByUsername("ksilva")).thenReturn(mockStaff);

        assertThrows(AuthenticationException.class,
                () -> authService.login("ksilva", "wrongpassword"));
    }

    private String sha256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}