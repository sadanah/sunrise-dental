package com.sunrisedentalclinic.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutApiServletTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession httpSession;

    private LogoutApiServlet servlet;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new LogoutApiServlet();
        stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void doPost_invalidatesSessionWhenPresent() throws Exception {
        when(request.getSession(false)).thenReturn(httpSession);

        servlet.doPost(request, response);

        verify(httpSession, times(1)).invalidate();
        verify(response).setStatus(200);
    }

    @Test
    void doPost_doesNotThrowWhenNoSessionExists() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        assertDoesNotThrow(() -> servlet.doPost(request, response));

        verify(response).setStatus(200);
    }

    @Test
    void doPost_returnsLoggedOutStatusMessage() throws Exception {
        when(request.getSession(false)).thenReturn(httpSession);

        servlet.doPost(request, response);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> body = mapper.readValue(stringWriter.toString(), Map.class);
        assertEquals("Logged out successfully", body.get("message"));
    }
}