package com.sunrisedentalclinic.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrisedentalclinic.dao.PatientDAO;
import com.sunrisedentalclinic.domain.Patient;
import com.sunrisedentalclinic.domain.Session;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientApiServletTest {

    @Mock private PatientDAO patientDAO;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession httpSession;

    private PatientApiServlet servlet;
    private StringWriter stringWriter;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        servlet = new PatientApiServlet(patientDAO);
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

    private void mockRequestBody(String json) throws Exception {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
    }

    // ===== doGet =====

    @Test
    void doGet_returns401WhenNotAuthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(401);
        verifyNoInteractions(patientDAO);
    }

    @Test
    void doGet_returnsSinglePatientWhenIdGivenAndFound() throws Exception {
        mockSession("RECEPTIONIST");
        when(request.getParameter("patientID")).thenReturn("P001");
        when(patientDAO.findById("P001")).thenReturn(
                new Patient(1, "Jane Doe", "0771234567", "Colombo", "P001", LocalDate.now()));

        servlet.doGet(request, response);

        verify(response).setStatus(200);
        Map<?, ?> body = mapper.readValue(stringWriter.toString(), Map.class);
        assertEquals("Jane Doe", body.get("name"));
    }

    @Test
    void doGet_returns404WhenIdGivenButNotFound() throws Exception {
        mockSession("RECEPTIONIST");
        when(request.getParameter("patientID")).thenReturn("P999");
        when(patientDAO.findById("P999")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(404);
    }

    @Test
    void doGet_returnsAllPatientsWhenNoIdGiven() throws Exception {
        mockSession("RECEPTIONIST");
        when(request.getParameter("patientID")).thenReturn(null);
        when(patientDAO.findAll()).thenReturn(List.of(
                new Patient(1, "Jane Doe", "0771234567", "Colombo", "P001", LocalDate.now()),
                new Patient(2, "John Smith", "0779876543", "Kandy", "P002", LocalDate.now())));

        servlet.doGet(request, response);

        verify(response).setStatus(200);
        List<?> body = mapper.readValue(stringWriter.toString(), List.class);
        assertEquals(2, body.size());
    }

    // ===== doPost =====

    @Test
    void doPost_returns403WhenRoleIsNeitherReceptionistNorAdmin() throws Exception {
        mockSession("DENTIST");

        servlet.doPost(request, response);

        verify(response).setStatus(403);
        verifyNoInteractions(patientDAO);
    }

    @Test
    void doPost_createsPatient_whenActionIsCreate() throws Exception {
        mockSession("RECEPTIONIST");
        mockRequestBody("{\"action\":\"create\",\"patientID\":\"P003\",\"name\":\"Amal Perera\",\"contactNo\":\"0712223334\",\"address\":\"Galle\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientDAO).save(captor.capture());
        assertEquals("P003", captor.getValue().getPatientID());
        assertEquals("Amal Perera", captor.getValue().getName());

        verify(response).setStatus(200);
    }

    @Test
    void doPost_createsPatient_whenActionIsAnythingOtherThanUpdateOrDelete() throws Exception {
        // The servlet's else-branch treats any action other than "delete"/"update" as create —
        // confirming that "REGISTER" (used historically in an earlier client draft) still works.
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"REGISTER\",\"patientID\":\"P004\",\"name\":\"Nadeeka\",\"contactNo\":\"0715556667\",\"address\":\"Matara\"}");

        servlet.doPost(request, response);

        verify(patientDAO).save(any(Patient.class));
        verify(response).setStatus(200);
    }

    @Test
    void doPost_updatesPatient_whenFoundAndActionIsUpdate() throws Exception {
        mockSession("RECEPTIONIST");
        Patient existing = new Patient(1, "Old Name", "0770000000", "Old Address", "P001", LocalDate.now());
        when(patientDAO.findById("P001")).thenReturn(existing);
        mockRequestBody("{\"action\":\"update\",\"patientID\":\"P001\",\"name\":\"New Name\",\"contactNo\":\"0771112223\",\"address\":\"New Address\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientDAO).update(captor.capture());
        assertEquals("New Name", captor.getValue().getName());
        assertEquals("0771112223", captor.getValue().getContactNo());
        assertEquals("New Address", captor.getValue().getAddress());

        verify(response).setStatus(200);
    }

    @Test
    void doPost_returns404WhenUpdateTargetNotFound() throws Exception {
        mockSession("RECEPTIONIST");
        when(patientDAO.findById("P999")).thenReturn(null);
        mockRequestBody("{\"action\":\"update\",\"patientID\":\"P999\",\"name\":\"X\",\"contactNo\":\"Y\",\"address\":\"Z\"}");

        servlet.doPost(request, response);

        verify(response).setStatus(404);
        verify(patientDAO, never()).update(any());
    }

    @Test
    void doPost_deletesPatient_whenActionIsDelete() throws Exception {
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"delete\",\"patientID\":\"P001\"}");

        servlet.doPost(request, response);

        verify(patientDAO).delete("P001");
        verify(response).setStatus(200);
    }

    @Test
    void doPost_returns400WhenRequestBodyIsMalformed() throws Exception {
        mockSession("RECEPTIONIST");
        mockRequestBody("not valid json");

        servlet.doPost(request, response);

        verify(response).setStatus(400);
        verify(patientDAO, never()).save(any());
    }
}