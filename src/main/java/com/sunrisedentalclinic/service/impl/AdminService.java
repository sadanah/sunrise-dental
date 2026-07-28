package com.sunrisedentalclinic.service.impl;

import com.sunrisedentalclinic.dao.StaffDAO;
import com.sunrisedentalclinic.dao.TreatmentTypeDAO;
import com.sunrisedentalclinic.domain.Staff;
import com.sunrisedentalclinic.domain.TreatmentType;
import com.sunrisedentalclinic.exception.AccessDeniedException;
import com.sunrisedentalclinic.report.Report;
import com.sunrisedentalclinic.report.ReportFactory;
import com.sunrisedentalclinic.service.IAdminService;
import com.sunrisedentalclinic.service.IAuthService;

import java.time.LocalDate;

public class AdminService implements IAdminService {

    private final StaffDAO staffDAO;
    private final TreatmentTypeDAO treatmentTypeDAO;
    private final ReportFactory reportFactory;
    private final IAuthService authService;

    public AdminService(StaffDAO staffDAO, TreatmentTypeDAO treatmentTypeDAO,
                        ReportFactory reportFactory, IAuthService authService) {
        this.staffDAO = staffDAO;
        this.treatmentTypeDAO = treatmentTypeDAO;
        this.reportFactory = reportFactory;
        this.authService = authService;
    }

    @Override
    public void manageStaff(String action, Staff staff) {
        switch (action) {
            case "create" -> staffDAO.save(staff);
            case "update" -> staffDAO.update(staff);
            case "delete" -> staffDAO.delete(staff.getStaffID());
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    @Override
    public void manageTreatment(String action, TreatmentType treatmentType) {
        switch (action) {
            case "create" -> treatmentTypeDAO.save(treatmentType);
            case "update" -> treatmentTypeDAO.update(treatmentType);
            case "delete" -> treatmentTypeDAO.delete(treatmentType.getTreatmentID());
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    @Override
    public Report generateReport(String type, String sessionID, LocalDate startDate, LocalDate endDate, String dentistID) {
        Staff currentUser = authService.getCurrentUser(sessionID);
        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new AccessDeniedException("Only Admin users can generate reports");
        }

        Report report = reportFactory.createReport(type, currentUser.getStaffID(), startDate, endDate, dentistID);
        report.generate();
        return report;
    }
}