package com.sunrisedentalclinic.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sunrisedentalclinic.client.dto.*;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/sunrise-dental-clinic";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public ApiClient() {
        this(HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build());
    }

    // package-private constructor for testing — allows injecting a mock HttpClient
    ApiClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static class ApiResponse<T> {
        public int statusCode;
        public T body;
        public String errorMessage;
    }

    private <T> ApiResponse<T> post(String path, Object requestBody, Class<T> responseType) throws Exception {
        String json = mapper.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return parse(response, responseType);
    }

    private <T> ApiResponse<T> get(String path, Class<T> responseType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return parse(response, responseType);
    }

    private <T> ApiResponse<T> delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return parse(response, null);
    }

    @SuppressWarnings("unchecked")
    private <T> ApiResponse<T> parse(HttpResponse<String> response, Class<T> responseType) throws Exception {
        ApiResponse<T> result = new ApiResponse<>();
        result.statusCode = response.statusCode();

        System.out.println("[DEBUG] status=" + response.statusCode() + " body=" + response.body());
        System.out.println("[DEBUG] headers=" + response.headers().map());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            if (responseType != null && response.body() != null && !response.body().isEmpty()) {
                result.body = mapper.readValue(response.body(), responseType);
            }
        } else {
            if (response.body() != null && !response.body().isEmpty()) {
                try {
                    Map<String, String> err = mapper.readValue(response.body(), Map.class);
                    result.errorMessage = err.getOrDefault("error", "Unknown error");
                } catch (Exception parseEx) {
                    result.errorMessage = "Server returned status " + response.statusCode()
                            + " with unparseable body: " + response.body();
                }
            } else {
                result.errorMessage = "Server returned status " + response.statusCode() + " with empty body";
            }
        }
        return result;
    }

    // ===== Auth =====
    public ApiResponse<SessionDto> login(String username, String password) throws Exception {
        return post("/api/login", Map.of("username", username, "password", password), SessionDto.class);
    }

    // ===== Appointments =====
    public ApiResponse<AppointmentDto> registerAppointment(String patientID, String dentistID, String treatmentID, String date, String time) throws Exception {
        return post("/api/appointments", Map.of(
                "patientID", patientID, "dentistID", dentistID, "treatmentID", treatmentID,
                "date", date, "time", time), AppointmentDto.class);
    }

    public ApiResponse<AppointmentDto> searchAppointment(String appointmentNo) throws Exception {
        return get("/api/appointments?appointmentNo=" + appointmentNo, AppointmentDto.class);
    }

    public ApiResponse<Void> cancelAppointment(String appointmentNo) throws Exception {
        return delete("/api/appointments?appointmentNo=" + appointmentNo);
    }

    public ApiResponse<AppointmentDto[]> getAllAppointments() throws Exception {
        return get("/api/appointments", AppointmentDto[].class);
    }

    // ===== Billing =====
    public ApiResponse<BillDto> generateBill(String appointmentNo, String discountPercent) throws Exception {
        return post("/api/bills", Map.of("appointmentNo", appointmentNo,
                "discountPercent", discountPercent == null ? "" : discountPercent), BillDto.class);
    }

    // ===== Patients =====
    public ApiResponse<PatientDto[]> getPatients() throws Exception {
        return get("/api/patients", PatientDto[].class);
    }

    public ApiResponse<Void> savePatient(Map<String, String> fields) throws Exception {
        return post("/api/patients", fields, Void.class);
    }

    // ===== Staff (Admin) =====
    public ApiResponse<StaffDto[]> getStaff() throws Exception {
        return get("/api/admin/staff", StaffDto[].class);
    }

    public ApiResponse<Void> saveStaff(Map<String, String> fields) throws Exception {
        return post("/api/admin/staff", fields, Void.class);
    }

    // ===== Treatments (Admin) =====
    public ApiResponse<TreatmentDto[]> getTreatments() throws Exception {
        return get("/api/admin/treatments", TreatmentDto[].class);
    }

    public ApiResponse<Void> saveTreatment(Map<String, String> fields) throws Exception {
        return post("/api/admin/treatments", fields, Void.class);
    }

    // ===== Reports (Admin) =====
    public ApiResponse<Map> generateReport(String type, String startDate, String endDate, String dentistID) throws Exception {
        return post("/api/admin/reports", Map.of("type", type,
                "startDate", startDate == null ? "" : startDate,
                "endDate", endDate == null ? "" : endDate,
                "dentistID", dentistID == null ? "" : dentistID), Map.class);
    }

    // ===== Help =====
    public ApiResponse<List> getHelpTopics() throws Exception {
        return get("/api/help", List.class);
    }

    public ApiResponse<Map> getHelpContent(String topic) throws Exception {
        return get("/api/help?topic=" + topic, Map.class);
    }

    // ===== Dentists =====
    public ApiResponse<DentistDto[]> getDentists() throws Exception {
        return get("/api/dentists", DentistDto[].class);
    }

    // ===== Auth (logout) =====
    public ApiResponse<Void> logout() throws Exception {
        return post("/api/logout", Map.of(), Void.class);
    }
}