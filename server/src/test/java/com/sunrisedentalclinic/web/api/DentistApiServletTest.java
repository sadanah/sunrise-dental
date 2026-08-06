package com.sunrisedentalclinic.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrisedentalclinic.dao.DentistDAO;
import com.sunrisedentalclinic.domain.Dentist;
import com.sunrisedentalclinic.domain.Session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DentistApiServletTest {

    @Mock private DentistDAO dentistDAO;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession httpSession;

    private DentistApiServlet servlet;
    private StringWriter stringWriter;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        servlet = new DentistApiServlet(dentistDAO);
        stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    private void mockSession(String role) {
        Session session = new Session("SESS1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        session.setStaffID("X001");
        session.setRole(role);
        when(request.getSession(false)).thenReturn(httpSession);
        when(httpSession.getAttribute("appSession")).thenReturn(session);
    }

    @Test
    void doGet_returns401WhenNotAuthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(401);
        verifyNoInteractions(dentistDAO);
    }

    @Test
    void doGet_returns200WithDentistListForAnyAuthenticatedRole() throws Exception {
        mockSession("RECEPTIONIST");
        when(dentistDAO.findAll()).thenReturn(List.of(
                new Dentist(1, "Dr. Perera", "0771234567", "Colombo", "D001", "nperera",
                        "hashed-password", "General", new BigDecimal("2500.00"))));

        servlet.doGet(request, response);

        verify(response).setStatus(200);
        List<?> body = mapper.readValue(stringWriter.toString(), List.class);
        assertEquals(1, body.size());
    }

    @Test
    void doGet_responseDoesNotLeakPasswordHash() throws Exception {
        mockSession("ADMIN");
        when(dentistDAO.findAll()).thenReturn(List.of(
                new Dentist(1, "Dr. Perera", "0771234567", "Colombo", "D001", "nperera",
                        "super-secret-hash", "General", new BigDecimal("2500.00"))));

        servlet.doGet(request, response);

        String json = stringWriter.toString();
        assertEquals(false, json.contains("super-secret-hash"));
        assertEquals(false, json.contains("passwordHash"));
    }
}