package com.sunrisedentalclinic.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.exception.AuthenticationException;
import com.sunrisedentalclinic.service.IAuthService;

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
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthApiServletTest {

    @Mock private IAuthService authService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession httpSession;

    private AuthApiServlet servlet;
    private StringWriter stringWriter;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AuthApiServlet(authService);
        stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    private void mockRequestBody(String json) throws Exception {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
    }

    @Test
    void doPost_returnsSessionAndSetsHttpSession_onValidCredentials() throws Exception {
        mockRequestBody("{\"username\":\"ksilva\",\"password\":\"pass123\"}");

        Session session = new Session("SESS1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        session.setStaffID("R001");
        session.setRole("RECEPTIONIST");
        when(authService.login("ksilva", "pass123")).thenReturn(session);
        when(request.getSession(true)).thenReturn(httpSession);

        servlet.doPost(request, response);

        verify(httpSession).setAttribute("appSession", session);
        verify(httpSession).setMaxInactiveInterval(30 * 60);
        verify(response).setStatus(200);

        Map<?, ?> body = mapper.readValue(stringWriter.toString(), Map.class);
        assertEquals("R001", body.get("staffID"));
        assertEquals("RECEPTIONIST", body.get("role"));
    }

    @Test
    void doPost_returns401_onInvalidCredentials() throws Exception {
        mockRequestBody("{\"username\":\"ksilva\",\"password\":\"wrongpass\"}");

        when(authService.login("ksilva", "wrongpass"))
                .thenThrow(new AuthenticationException("Invalid username or password"));

        servlet.doPost(request, response);

        verify(response).setStatus(401);
        verify(request, never()).getSession(true);
        Map<?, ?> body = mapper.readValue(stringWriter.toString(), Map.class);
        assertEquals("Invalid username or password", body.get("error"));
    }
}