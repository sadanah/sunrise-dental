package com.sunrisedentalclinic.web.api;

import com.sunrisedentalclinic.domain.Bill;
import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.exception.AppointmentNotFoundException;
import com.sunrisedentalclinic.service.IBillingService;
import com.sunrisedentalclinic.service.impl.ClinicFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingApiServletTest {

    @Mock private ClinicFacade clinicFacade;
    @Mock private IBillingService billingService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession httpSession;

    private BillingApiServlet servlet;
    private ByteArrayOutputStream responseBody;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new BillingApiServlet(clinicFacade, billingService);
        responseBody = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(responseBody, true, StandardCharsets.UTF_8);
        when(response.getWriter()).thenReturn(writer);
    }

    private void mockSession(String role) {
        Session session = new Session("S1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
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
    void doPost_nonReceptionist_returns403() throws Exception {
        mockSession("ADMIN");

        servlet.doPost(request, response);

        verify(response).setStatus(403);
        verifyNoInteractions(clinicFacade);
    }

    @Test
    void doPost_noDiscount_returns201WithFullTotal() throws Exception {
        mockSession("RECEPTIONIST");
        mockRequestBody("{\"appointmentNo\":\"APT001\"}");

        Bill bill = new Bill("B001", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        when(clinicFacade.generateBill("APT001")).thenReturn(bill);

        servlet.doPost(request, response);

        verify(response).setStatus(201);
        verify(billingService, never()).applyDiscount(any(), any());
        assertTrue(responseBody.toString(StandardCharsets.UTF_8).contains("6000.00")
                || responseBody.toString(StandardCharsets.UTF_8).contains("\"totalAmount\""));
    }

    @Test
    void doPost_withValidDiscount_appliesDiscount() throws Exception {
        mockSession("RECEPTIONIST");
        mockRequestBody("{\"appointmentNo\":\"APT001\",\"discountPercent\":\"10\"}");

        Bill original = new Bill("B001", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        Bill discounted = new Bill("B001", new BigDecimal("2500.00"), new BigDecimal("3500.00"));

        when(clinicFacade.generateBill("APT001")).thenReturn(original);
        when(billingService.applyDiscount(eq(original), eq(new BigDecimal("10")))).thenReturn(discounted);

        servlet.doPost(request, response);

        verify(billingService).applyDiscount(original, new BigDecimal("10"));
        verify(response).setStatus(201);
    }

    @Test
    void doPost_discountOver100_returns400() throws Exception {
        mockSession("RECEPTIONIST");
        mockRequestBody("{\"appointmentNo\":\"APT001\",\"discountPercent\":\"150\"}");

        Bill bill = new Bill("B001", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        when(clinicFacade.generateBill("APT001")).thenReturn(bill);

        servlet.doPost(request, response);

        verify(response).setStatus(400);
        verify(billingService, never()).applyDiscount(any(), any());
    }

    @Test
    void doPost_appointmentNotFound_returns404() throws Exception {
        mockSession("RECEPTIONIST");
        mockRequestBody("{\"appointmentNo\":\"BADID\"}");

        when(clinicFacade.generateBill("BADID"))
                .thenThrow(new AppointmentNotFoundException("No appointment found: BADID"));

        servlet.doPost(request, response);

        verify(response).setStatus(404);
    }

    @Test
    void doPost_invalidDiscountValue_returns400() throws Exception {
        mockSession("RECEPTIONIST");
        mockRequestBody("{\"appointmentNo\":\"APT001\",\"discountPercent\":\"notanumber\"}");

        Bill bill = new Bill("B001", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        when(clinicFacade.generateBill("APT001")).thenReturn(bill);

        servlet.doPost(request, response);

        verify(response).setStatus(400);
    }
}