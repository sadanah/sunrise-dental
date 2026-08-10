package com.sunrisedentalclinic.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrisedentalclinic.dao.TreatmentTypeDAO;
import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.domain.TreatmentType;
import com.sunrisedentalclinic.service.IAdminService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreatmentApiServletTest {

    @Mock private IAdminService adminService;
    @Mock private TreatmentTypeDAO treatmentTypeDAO;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession httpSession;

    private TreatmentApiServlet servlet;
    private StringWriter stringWriter;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        servlet = new TreatmentApiServlet(adminService, treatmentTypeDAO);
        stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    private Session sessionWithRole(String role) {
        Session session = new Session("SESS1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        session.setStaffID("X001");
        session.setRole(role);
        return session;
    }

    private void mockSession(String role) {
        when(request.getSession(false)).thenReturn(httpSession);
        when(httpSession.getAttribute("appSession")).thenReturn(sessionWithRole(role));
    }

    private void mockRequestBody(String json) throws Exception {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
    }

    // ===== doGet =====

    @Test
    void doGet_returns403WhenNoSession() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(403);
        verifyNoInteractions(treatmentTypeDAO);
    }

    @Test
    void doGet_returns403WhenRoleIsNeitherAdminNorReceptionist() throws Exception {
        mockSession("DENTIST");

        servlet.doGet(request, response);

        verify(response).setStatus(403);
        verifyNoInteractions(treatmentTypeDAO);
    }

    @Test
    void doGet_returns200WithListWhenAdmin() throws Exception {
        mockSession("ADMIN");
        when(treatmentTypeDAO.findAll()).thenReturn(
                List.of(new TreatmentType("T001", "Cleaning", new BigDecimal("3500.00"))));

        servlet.doGet(request, response);

        verify(response).setStatus(200);
        List<?> body = mapper.readValue(stringWriter.toString(), List.class);
        assertEquals(1, body.size());
    }

    @Test
    void doGet_returns200WithListWhenReceptionist() throws Exception {
        mockSession("RECEPTIONIST");
        when(treatmentTypeDAO.findAll()).thenReturn(List.of());

        servlet.doGet(request, response);

        verify(response).setStatus(200);
    }

    // ===== doPost =====

    @Test
    void doPost_returns403WhenNotAdmin() throws Exception {
        mockSession("RECEPTIONIST");

        servlet.doPost(request, response);

        verify(response).setStatus(403);
        verifyNoInteractions(adminService);
    }

    @Test
    void doPost_createsTreatment_whenActionIsCreate() throws Exception {
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"create\",\"treatmentID\":\"T002\",\"treatmentName\":\"Filling\",\"baseCost\":\"1200.00\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<TreatmentType> captor = ArgumentCaptor.forClass(TreatmentType.class);
        verify(adminService).manageTreatment(eq("create"), captor.capture());
        assertEquals("T002", captor.getValue().getTreatmentID());
        assertEquals("Filling", captor.getValue().getTreatmentName());
        assertEquals(new BigDecimal("1200.00"), captor.getValue().getBaseCost());

        verify(response).setStatus(200);
        Map<?, ?> body = mapper.readValue(stringWriter.toString(), Map.class);
        assertEquals("ok", body.get("status"));
    }

    @Test
    void doPost_deletesTreatment_whenActionIsDelete() throws Exception {
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"delete\",\"treatmentID\":\"T002\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<TreatmentType> captor = ArgumentCaptor.forClass(TreatmentType.class);
        verify(adminService).manageTreatment(eq("delete"), captor.capture());
        assertEquals("T002", captor.getValue().getTreatmentID());

        verify(response).setStatus(200);
    }

    @Test
    void doPost_returns400WhenBaseCostIsInvalid() throws Exception {
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"create\",\"treatmentID\":\"T003\",\"treatmentName\":\"X-Ray\",\"baseCost\":\"not-a-number\"}");

        servlet.doPost(request, response);

        verify(response).setStatus(400);
        verify(adminService, never()).manageTreatment(anyString(), any());
    }
}