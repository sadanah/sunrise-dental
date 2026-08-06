package com.sunrisedentalclinic.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.service.IHelpService;

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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelpApiServletTest {

    @Mock private IHelpService helpService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession httpSession;

    private HelpApiServlet servlet;
    private StringWriter stringWriter;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        servlet = new HelpApiServlet(helpService);
        stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    private void mockAuthenticatedSession() {
        Session session = new Session("SESS1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        session.setStaffID("X001");
        session.setRole("RECEPTIONIST");
        when(request.getSession(false)).thenReturn(httpSession);
        when(httpSession.getAttribute("appSession")).thenReturn(session);
    }

    @Test
    void doGet_returns401WhenNotAuthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(401);
        verifyNoInteractions(helpService);
    }

    @Test
    void doGet_returnsTopicListWhenNoTopicParamGiven() throws Exception {
        mockAuthenticatedSession();
        when(request.getParameter("topic")).thenReturn(null);
        when(helpService.listHelpTopics()).thenReturn(List.of("Booking", "Billing", "Cancellation"));

        servlet.doGet(request, response);

        verify(response).setStatus(200);
        List<?> body = mapper.readValue(stringWriter.toString(), List.class);
        assertEquals(3, body.size());
        assertEquals("Booking", body.get(0));
    }

    @Test
    void doGet_returnsTopicContentWhenTopicParamGiven() throws Exception {
        mockAuthenticatedSession();
        when(request.getParameter("topic")).thenReturn("Billing");
        when(helpService.displayHelp("Billing")).thenReturn("Here is how billing works...");

        servlet.doGet(request, response);

        verify(response).setStatus(200);
        Map<?, ?> body = mapper.readValue(stringWriter.toString(), Map.class);
        assertEquals("Here is how billing works...", body.get("content"));
        verify(helpService).displayHelp("Billing");
        verify(helpService, never()).listHelpTopics();
    }
}