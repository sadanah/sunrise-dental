package com.sunrisedentalclinic.web.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Requires a running Tomcat instance at localhost:8080, started with " +
        "-Ddb.url pointing at sunrise_dental — run manually, not part of CI")
class LoginApiIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080/sunrise-dental-clinic";

    @Test
    void login_validCredentials_returns200WithSessionJson() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String requestBody = "{\"username\":\"receptionist1\",\"password\":\"pwd123\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("sessionID"));
        assertTrue(response.body().contains("\"role\":\"RECEPTIONIST\""));
        assertTrue(response.body().contains("\"valid\":true"));

        // Confirms the ISO-8601 date fix — should NOT be serialized as a numeric array
        assertFalse(response.body().matches("(?s).*\"loginTime\":\\s*\\[.*"),
                "loginTime should be an ISO-8601 string, not a numeric array");
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String requestBody = "{\"username\":\"ksilva\",\"password\":\"wrongpassword\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("error"));
        assertTrue(response.body().contains("Invalid username or password"));
    }
}