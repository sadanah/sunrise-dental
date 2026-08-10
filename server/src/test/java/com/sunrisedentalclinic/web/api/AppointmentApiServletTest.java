package com.sunrisedentalclinic.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.exception.AppointmentNotFoundException;
import com.sunrisedentalclinic.exception.SlotUnavailableException;
import com.sunrisedentalclinic.service.impl.ClinicFacade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentApiServletTest {

    @Mock private ClinicFacade clinicFacade;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession httpSession;

    private AppointmentApiServlet servlet;
    private StringWriter stringWriter;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AppointmentApiServlet(clinicFacade);
        stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    private void mockSession(String role) {
        Session session = new Session("SESS1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        session.setStaffID("R001");
        session.setRole(role);
        when(request.getSession(false)).thenReturn(httpSession);
        when(httpSession.getAttribute("appSession")).thenReturn(session);
    }

    private void mockRequestBody(String json) throws Exception {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
    }

    // ===== doPost =====

    @Test
    void doPost_returns403WhenNotReceptionist() throws Exception {
        mockSession("ADMIN");

        servlet.doPost(request, response);

        verify(response).setStatus(403);
        verifyNoInteractions(clinicFacade);
    }

    @Test
    void doPost_registersAppointment_onSuccess() throws Exception {
        mockSession("RECEPTIONIST");
        mockRequestBody("{\"patientID\":\"P001\",\"dentistID\":\"D001\",\"treatmentID\":\"T001\",\"date\":\"2026-09-01\",\"time\":\"10:00\"}");

        Appointment created = new Appointment("APT001", LocalDate.of(2026, 9, 1), LocalTime.of(10, 0));
        created.setPatientID("P001");
        created.setDentistID("D001");
        created.setTreatmentID("T001");
        created.setStaffID("R001");

        when(clinicFacade.registerAppointment("P001", "D001", "T001", "R001",
                LocalDate.of(2026, 9, 1), LocalTime.of(10, 0))).thenReturn(created);

        servlet.doPost(request, response);

        verify(response).setStatus(201);
        Map<?, ?> body = mapper.readValue(stringWriter.toString(), Map.class);
        assertEquals("APT001", body.get("appointmentNo"));
    }

    @Test
    void doPost_returns409WhenSlotUnavailable() throws Exception {
        mockSession("RECEPTIONIST");
        mockRequestBody("{\"patientID\":\"P001\",\"dentistID\":\"D001\",\"treatmentID\":\"T001\",\"date\":\"2026-09-01\",\"time\":\"10:00\"}");

        when(clinicFacade.registerAppointment(anyString(), anyString(), anyString(), anyString(), any(), any()))
                .thenThrow(new SlotUnavailableException("This dentist already has an appointment at this time"));

        servlet.doPost(request, response);

        verify(response).setStatus(409);
    }

    @Test
    void doPost_returns400WhenRequestBodyIsMalformed() throws Exception {
        mockSession("RECEPTIONIST");
        mockRequestBody("not valid json");

        servlet.doPost(request, response);

        verify(response).setStatus(400);
        verifyNoInteractions(clinicFacade);
    }

    // ===== doGet =====

    @Test
    void doGet_returns401WhenNotAuthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(401);
        verifyNoInteractions(clinicFacade);
    }

    @Test
    void doGet_returnsSingleAppointment_whenAppointmentNoGivenAndFound() throws Exception {
        mockSession("RECEPTIONIST");
        when(request.getParameter("appointmentNo")).thenReturn("APT001");
        Appointment appt = new Appointment("APT001", LocalDate.now(), LocalTime.of(10, 0));
        when(clinicFacade.searchAppointment("APT001")).thenReturn(appt);

        servlet.doGet(request, response);

        verify(response).setStatus(200);
        Map<?, ?> body = mapper.readValue(stringWriter.toString(), Map.class);
        assertEquals("APT001", body.get("appointmentNo"));
    }

    @Test
    void doGet_returns404_whenAppointmentNoGivenButNotFound() throws Exception {
        mockSession("RECEPTIONIST");
        when(request.getParameter("appointmentNo")).thenReturn("APT999");
        when(clinicFacade.searchAppointment("APT999")).thenThrow(new AppointmentNotFoundException("Not found"));

        servlet.doGet(request, response);

        verify(response).setStatus(404);
    }

    @Test
    void doGet_returnsAllAppointments_whenNoAppointmentNoGiven() throws Exception {
        mockSession("RECEPTIONIST");
        when(request.getParameter("appointmentNo")).thenReturn(null);
        when(clinicFacade.getAllAppointments()).thenReturn(List.of(
                new Appointment("APT001", LocalDate.now(), LocalTime.of(9, 0)),
                new Appointment("APT002", LocalDate.now(), LocalTime.of(10, 0))));

        servlet.doGet(request, response);

        verify(response).setStatus(200);
        List<?> body = mapper.readValue(stringWriter.toString(), List.class);
        assertEquals(2, body.size());
    }

    // ===== doDelete =====

    @Test
    void doDelete_returns403WhenNotReceptionist() throws Exception {
        mockSession("ADMIN");

        servlet.doDelete(request, response);

        verify(response).setStatus(403);
        verifyNoInteractions(clinicFacade);
    }

    @Test
    void doDelete_cancelsAppointment_onSuccess() throws Exception {
        mockSession("RECEPTIONIST");
        when(request.getParameter("appointmentNo")).thenReturn("APT001");

        servlet.doDelete(request, response);

        verify(clinicFacade).cancelAppointment("APT001");
        verify(response).setStatus(200);
    }

    @Test
    void doDelete_returns400WhenCancelThrows() throws Exception {
        mockSession("RECEPTIONIST");
        when(request.getParameter("appointmentNo")).thenReturn("APT999");
        doThrow(new RuntimeException("Appointment not found")).when(clinicFacade).cancelAppointment("APT999");

        servlet.doDelete(request, response);

        verify(response).setStatus(400);
    }
}