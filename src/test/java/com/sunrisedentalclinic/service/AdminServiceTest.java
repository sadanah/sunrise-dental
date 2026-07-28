package com.sunrisedentalclinic.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import com.sunrisedentalclinic.dao.StaffDAO;
import com.sunrisedentalclinic.dao.TreatmentTypeDAO;
import com.sunrisedentalclinic.domain.Admin;
import com.sunrisedentalclinic.domain.Receptionist;
import com.sunrisedentalclinic.exception.AccessDeniedException;
import com.sunrisedentalclinic.report.ReportFactory;
import com.sunrisedentalclinic.service.impl.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private StaffDAO staffDAO;
    @Mock private TreatmentTypeDAO treatmentTypeDAO;
    @Mock private ReportFactory reportFactory;
    @Mock private IAuthService authService;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(staffDAO, treatmentTypeDAO, reportFactory, authService);
    }

    @Test
    void generateReport_throwsAccessDeniedWhenUserIsNotAdmin() {
        Receptionist notAdmin = new Receptionist(1, "Kasun", "0771234567", "Colombo", "R001", "ksilva", "hash");
        when(authService.getCurrentUser("session123")).thenReturn(notAdmin);

        assertThrows(AccessDeniedException.class, () ->
                adminService.generateReport("REVENUE", "session123", LocalDate.now().minusDays(7), LocalDate.now(), null));
    }

    @Test
    void generateReport_succeedsWhenUserIsAdmin() {
        Admin admin = new Admin(1, "Amali", "0761122334", "Colombo", "A001", "afernando", "hash");
        when(authService.getCurrentUser("session456")).thenReturn(admin);
        when(reportFactory.createReport(anyString(), anyString(), any(), any(), any()))
                .thenReturn(mock(com.sunrisedentalclinic.report.Report.class));

        assertDoesNotThrow(() ->
                adminService.generateReport("REVENUE", "session456", LocalDate.now().minusDays(7), LocalDate.now(), null));
    }
}