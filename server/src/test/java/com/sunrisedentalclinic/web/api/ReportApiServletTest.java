package com.sunrisedentalclinic.web.api;

import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.exception.AccessDeniedException;
import com.sunrisedentalclinic.report.RevenueReport;
import com.sunrisedentalclinic.service.IAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportApiServletTest {

    @Mock private IAdminService adminService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession httpSession;

    private ReportApiServlet servlet;
    private ByteArrayOutputStream responseBody;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ReportApiServlet(adminService);
        responseBody = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(responseBody, true, StandardCharsets.UTF_8);
        when(response.getWriter()).thenReturn(writer);
    }

    private void mockSession(String role, String sessionID) {
        Session session = new Session(sessionID, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        session.setRole(role);
        when(request.getSession(false)).thenReturn(httpSession);
        when(httpSession.getAttribute("appSession")).thenReturn(session);
    }

    private void mockRequestBody(String json) throws Exception {
        ServletInputStream sis = new ServletInputStream() {
            private final ByteArrayInputStream delegate = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            public int read() { return delegate.read(); }
            public boolean isFinished() { return delegate.available() == 0; }
            public boolean isReady() { return true; }
            public void setReadListener(javax.servlet.ReadListener readListener) {}
        };
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new java.io.InputStreamReader(sis)));
    }

    @Test
    void doPost_nonAdminRole_servletLevelReturns403() throws Exception {
        mockSession("RECEPTIONIST", "S1");

        servlet.doPost(request, response);

        verify(response).setStatus(403);
        verifyNoInteractions(adminService);
    }

    @Test
    void doPost_adminRole_revenueReport_returns200() throws Exception {
        mockSession("ADMIN", "S1");
        mockRequestBody("{\"type\":\"REVENUE\",\"startDate\":\"2026-07-01\",\"endDate\":\"2026-08-04\",\"dentistID\":\"\"}");

        RevenueReport mockReport = mock(RevenueReport.class);
        when(adminService.generateReport(eq("REVENUE"), eq("S1"),
                eq(LocalDate.of(2026, 7, 1)), eq(LocalDate.of(2026, 8, 4)), eq("")))
                .thenReturn(mockReport);

        servlet.doPost(request, response);

        verify(response).setStatus(200);
        verify(adminService).generateReport(eq("REVENUE"), eq("S1"), any(), any(), any());
    }

    @Test
    void doPost_serviceLayerDeniesAccess_returns403() throws Exception {
        // Defense-in-depth: even if servlet check somehow passed, service layer's own
        // independent role check should also be respected and surfaced correctly.
        mockSession("ADMIN", "S1");
        mockRequestBody("{\"type\":\"REVENUE\",\"startDate\":\"2026-07-01\",\"endDate\":\"2026-08-04\",\"dentistID\":\"\"}");

        when(adminService.generateReport(any(), any(), any(), any(), any()))
                .thenThrow(new AccessDeniedException("Only Admin users can generate reports"));

        servlet.doPost(request, response);

        verify(response).setStatus(403);
    }

    @Test
    void doPost_missingDates_treatedAsNull() throws Exception {
        mockSession("ADMIN", "S1");
        mockRequestBody("{\"type\":\"DAILY_APPOINTMENTS\",\"startDate\":\"\",\"endDate\":\"\",\"dentistID\":\"\"}");

        RevenueReport mockReport = mock(RevenueReport.class);
        when(adminService.generateReport(eq("DAILY_APPOINTMENTS"), eq("S1"), isNull(), isNull(), eq("")))
                .thenReturn(mockReport);

        servlet.doPost(request, response);

        verify(response).setStatus(200);
    }
}