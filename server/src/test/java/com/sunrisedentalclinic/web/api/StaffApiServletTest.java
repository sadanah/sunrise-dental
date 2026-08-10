package com.sunrisedentalclinic.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrisedentalclinic.dao.StaffDAO;
import com.sunrisedentalclinic.domain.*;
import com.sunrisedentalclinic.service.IAdminService;
import com.sunrisedentalclinic.util.PasswordUtil;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffApiServletTest {

    @Mock private IAdminService adminService;
    @Mock private StaffDAO staffDAO;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession httpSession;

    private StaffApiServlet servlet;
    private StringWriter stringWriter;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        servlet = new StaffApiServlet(adminService, staffDAO);
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
    void doGet_returns403WhenNotAdmin() throws Exception {
        mockSession("RECEPTIONIST");

        servlet.doGet(request, response);

        verify(response).setStatus(403);
        verifyNoInteractions(staffDAO);
    }

    @Test
    void doGet_returns200WithListWhenAdmin() throws Exception {
        mockSession("ADMIN");
        when(staffDAO.findAll()).thenReturn(List.of(
                new Receptionist(1, "Kasun", "0771234567", "Colombo", "R001", "ksilva", "hash")));

        servlet.doGet(request, response);

        verify(response).setStatus(200);
        List<?> body = mapper.readValue(stringWriter.toString(), List.class);
        assertEquals(1, body.size());
    }

    // ===== doPost: role guard =====

    @Test
    void doPost_returns403WhenNotAdmin() throws Exception {
        mockSession("RECEPTIONIST");

        servlet.doPost(request, response);

        verify(response).setStatus(403);
        verifyNoInteractions(adminService);
    }

    // ===== doPost: create =====

    @Test
    void doPost_createsReceptionist_whenActionIsCreate() throws Exception {
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"create\",\"role\":\"RECEPTIONIST\",\"staffID\":\"R002\",\"name\":\"Nadeeka\",\"contactNo\":\"0712223334\",\"address\":\"Galle\",\"username\":\"nperera\",\"password\":\"pass123\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<Staff> captor = ArgumentCaptor.forClass(Staff.class);
        verify(adminService).manageStaff(eq("create"), captor.capture());
        Staff created = captor.getValue();
        assertInstanceOf(Receptionist.class, created);
        assertEquals("R002", created.getStaffID());
        assertEquals(PasswordUtil.hash("pass123"), created.getPasswordHash());

        verify(response).setStatus(200);
    }

    @Test
    void doPost_createsDentist_withSpecializationAndFee() throws Exception {
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"create\",\"role\":\"DENTIST\",\"staffID\":\"D002\",\"name\":\"Dr. Fernando\",\"contactNo\":\"0712223334\",\"address\":\"Galle\",\"username\":\"dfernando\",\"password\":\"pass123\",\"specialization\":\"Orthodontics\",\"consultationFee\":\"3000.00\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<Staff> captor = ArgumentCaptor.forClass(Staff.class);
        verify(adminService).manageStaff(eq("create"), captor.capture());
        Staff created = captor.getValue();
        assertInstanceOf(Dentist.class, created);
        Dentist dentist = (Dentist) created;
        assertEquals("Orthodontics", dentist.getSpecialization());
        assertEquals(new BigDecimal("3000.00"), dentist.getConsultationFee());

        verify(response).setStatus(200);
    }

    @Test
    void doPost_createsAdmin_whenRoleIsAdmin() throws Exception {
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"create\",\"role\":\"ADMIN\",\"staffID\":\"A002\",\"name\":\"Priya\",\"contactNo\":\"0712223334\",\"address\":\"Galle\",\"username\":\"priya\",\"password\":\"pass123\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<Staff> captor = ArgumentCaptor.forClass(Staff.class);
        verify(adminService).manageStaff(eq("create"), captor.capture());
        assertInstanceOf(Admin.class, captor.getValue());
    }

    @Test
    void doPost_returns400WhenConsultationFeeIsNotANumber() throws Exception {
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"create\",\"role\":\"DENTIST\",\"staffID\":\"D003\",\"name\":\"Dr. X\",\"contactNo\":\"0712223334\",\"address\":\"Galle\",\"username\":\"drx\",\"password\":\"pass123\",\"specialization\":\"General\",\"consultationFee\":\"not-a-number\"}");

        servlet.doPost(request, response);

        verify(response).setStatus(400);
        verify(adminService, never()).manageStaff(anyString(), any());
    }

    // ===== doPost: update =====

    @Test
    void doPost_update_rehashesPassword_whenPasswordProvided() throws Exception {
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"update\",\"role\":\"RECEPTIONIST\",\"staffID\":\"R001\",\"name\":\"Kasun\",\"contactNo\":\"0771234567\",\"address\":\"Colombo\",\"username\":\"ksilva\",\"password\":\"newpass\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<Staff> captor = ArgumentCaptor.forClass(Staff.class);
        verify(adminService).manageStaff(eq("update"), captor.capture());
        assertEquals(PasswordUtil.hash("newpass"), captor.getValue().getPasswordHash());
        verifyNoInteractions(staffDAO); // shouldn't need to look up existing hash when a new one is provided

        verify(response).setStatus(200);
    }

    @Test
    void doPost_update_keepsExistingPasswordHash_whenPasswordBlank() throws Exception {
        mockSession("ADMIN");
        Receptionist existing = new Receptionist(1, "Kasun", "0771234567", "Colombo", "R001", "ksilva", "existing-hash");
        when(staffDAO.findById("R001")).thenReturn(existing);
        mockRequestBody("{\"action\":\"update\",\"role\":\"RECEPTIONIST\",\"staffID\":\"R001\",\"name\":\"Kasun\",\"contactNo\":\"0771234567\",\"address\":\"Colombo\",\"username\":\"ksilva\",\"password\":\"\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<Staff> captor = ArgumentCaptor.forClass(Staff.class);
        verify(adminService).manageStaff(eq("update"), captor.capture());
        assertEquals("existing-hash", captor.getValue().getPasswordHash());

        verify(response).setStatus(200);
    }

    @Test
    void doPost_update_keepsExistingPasswordHash_whenPasswordFieldMissing() throws Exception {
        mockSession("ADMIN");
        Receptionist existing = new Receptionist(1, "Kasun", "0771234567", "Colombo", "R001", "ksilva", "existing-hash");
        when(staffDAO.findById("R001")).thenReturn(existing);
        mockRequestBody("{\"action\":\"update\",\"role\":\"RECEPTIONIST\",\"staffID\":\"R001\",\"name\":\"Kasun\",\"contactNo\":\"0771234567\",\"address\":\"Colombo\",\"username\":\"ksilva\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<Staff> captor = ArgumentCaptor.forClass(Staff.class);
        verify(adminService).manageStaff(eq("update"), captor.capture());
        assertEquals("existing-hash", captor.getValue().getPasswordHash());
    }

    // ===== doPost: delete =====

    @Test
    void doPost_deletesStaff_whenActionIsDelete() throws Exception {
        mockSession("ADMIN");
        mockRequestBody("{\"action\":\"delete\",\"staffID\":\"R001\"}");

        servlet.doPost(request, response);

        ArgumentCaptor<Staff> captor = ArgumentCaptor.forClass(Staff.class);
        verify(adminService).manageStaff(eq("delete"), captor.capture());
        assertEquals("R001", captor.getValue().getStaffID());

        verify(response).setStatus(200);
    }
}