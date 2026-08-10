package com.sunrisedentalclinic.client;

import com.sunrisedentalclinic.client.dto.AppointmentDto;
import com.sunrisedentalclinic.client.dto.PatientDto;
import com.sunrisedentalclinic.client.dto.SessionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpHeaders;
import java.util.Map;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiClientTest {

    private HttpClient mockHttpClient;
    private ApiClient apiClient;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        apiClient = new ApiClient(mockHttpClient);
    }

    @SuppressWarnings("unchecked")
    private void stubResponse(int statusCode, String body) throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(statusCode);
        when(mockResponse.body()).thenReturn(body);
        when(mockResponse.headers()).thenReturn(HttpHeaders.of(Map.of(), (k, v) -> true));
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
    }

    @Test
    void login_success_parsesSessionDto() throws Exception {
        stubResponse(200, "{\"sessionID\":\"abc123\",\"staffID\":\"S001\",\"role\":\"RECEPTIONIST\",\"valid\":true}");

        ApiClient.ApiResponse<SessionDto> result = apiClient.login("s001", "pw");

        assertEquals(200, result.statusCode);
        assertNotNull(result.body);
        assertEquals("S001", result.body.getStaffID());
        assertEquals("RECEPTIONIST", result.body.getRole());
        assertNull(result.errorMessage);
    }

    @Test
    void login_authenticationFailure_parsesErrorMessage() throws Exception {
        stubResponse(401, "{\"error\":\"Invalid username or password\"}");

        ApiClient.ApiResponse<SessionDto> result = apiClient.login("bad", "creds");

        assertEquals(401, result.statusCode);
        assertNull(result.body);
        assertEquals("Invalid username or password", result.errorMessage);
    }

    @Test
    void parse_unparseableErrorBody_fallsBackToRawMessage() throws Exception {
        stubResponse(500, "<html>Internal Server Error</html>");

        ApiClient.ApiResponse<SessionDto> result = apiClient.login("s001", "pw");

        assertEquals(500, result.statusCode);
        assertTrue(result.errorMessage.contains("500"));
        assertTrue(result.errorMessage.contains("unparseable"));
    }

    @Test
    void parse_emptyErrorBody_reportsEmptyBodyMessage() throws Exception {
        stubResponse(403, "");

        ApiClient.ApiResponse<SessionDto> result = apiClient.login("s001", "pw");

        assertEquals(403, result.statusCode);
        assertEquals("Server returned status 403 with empty body", result.errorMessage);
    }

    @Test
    void getPatients_success_parsesArray() throws Exception {
        stubResponse(200, "[{\"patientID\":\"P001\",\"name\":\"John Silva\"},"
                + "{\"patientID\":\"P002\",\"name\":\"Amaya Perera\"}]");

        ApiClient.ApiResponse<PatientDto[]> result = apiClient.getPatients();

        assertEquals(200, result.statusCode);
        assertEquals(2, result.body.length);
        assertEquals("P001", result.body[0].getPatientID());
    }

    @Test
    void registerAppointment_slotUnavailable_returns409WithMessage() throws Exception {
        stubResponse(409, "{\"error\":\"Slot already booked for this dentist and time\"}");

        ApiClient.ApiResponse<AppointmentDto> result = apiClient.registerAppointment(
                "P001", "S001", "T001", "2026-08-10", "10:00");

        assertEquals(409, result.statusCode);
        assertEquals("Slot already booked for this dentist and time", result.errorMessage);
    }

    @Test
    void cancelAppointment_success_noBodyExpected() throws Exception {
        stubResponse(200, "{\"status\":\"cancelled\"}");

        ApiClient.ApiResponse<Void> result = apiClient.cancelAppointment("APT001");

        assertEquals(200, result.statusCode);
        assertNull(result.errorMessage);
    }

    @Test
    void unknownJsonFields_doNotBreakDeserialization() throws Exception {
        // domain objects (e.g. Staff/Dentist) leak extra fields like personId/details —
        // confirms FAIL_ON_UNKNOWN_PROPERTIES=false is actually in effect
        stubResponse(200, "{\"sessionID\":\"abc\",\"staffID\":\"S001\",\"role\":\"RECEPTIONIST\","
                + "\"valid\":true,\"someUnexpectedField\":\"ignored\"}");

        ApiClient.ApiResponse<SessionDto> result = apiClient.login("s001", "pw");

        assertEquals(200, result.statusCode);
        assertEquals("S001", result.body.getStaffID());
    }

    @Test
    void savePatient_success_sendsMapAndReturnsOk() throws Exception {
        stubResponse(200, "{\"status\":\"ok\"}");

        Map<String, String> fields = new java.util.HashMap<>();
        fields.put("action", "create");
        fields.put("patientID", "P003");
        fields.put("name", "Nadeeka Perera");
        fields.put("contactNo", "0771234567");
        fields.put("address", "Colombo");
        fields.put("email", "nadeeka@example.com");

        ApiClient.ApiResponse<Void> result = apiClient.savePatient(fields);

        assertEquals(200, result.statusCode);
        assertNull(result.errorMessage);
    }
}