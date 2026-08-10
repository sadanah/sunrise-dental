package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.domain.Staff;
import com.sunrisedentalclinic.domain.TreatmentType;
import com.sunrisedentalclinic.report.Report;
import java.time.LocalDate;

public interface IAdminService {
    void manageStaff(String action, Staff staff);
    void manageTreatment(String action, TreatmentType treatmentType);
    Report generateReport(String type, String sessionID, LocalDate startDate, LocalDate endDate, String dentistID);
}