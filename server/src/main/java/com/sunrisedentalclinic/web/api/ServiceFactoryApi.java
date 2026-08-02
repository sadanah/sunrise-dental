package com.sunrisedentalclinic.web.api;

import com.sunrisedentalclinic.dao.*;
import com.sunrisedentalclinic.report.ReportFactory;
import com.sunrisedentalclinic.service.*;
import com.sunrisedentalclinic.service.impl.*;

public class ServiceFactoryApi {
    private static final StaffDAO staffDAO = new StaffDAO();
    private static final PatientDAO patientDAO = new PatientDAO();
    private static final DentistDAO dentistDAO = new DentistDAO();
    private static final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private static final BillDAO billDAO = new BillDAO();
    private static final TreatmentTypeDAO treatmentTypeDAO = new TreatmentTypeDAO();
    private static final EmailGateway emailGateway = new EmailGateway();
    private static final SmsGateway smsGateway = new SmsGateway();
    private static final IAuthService authService = new AuthenticationService(staffDAO);
    private static final INotificationService notificationService = new NotificationService(emailGateway, smsGateway);
    private static final IAppointmentService appointmentService = new AppointmentService(appointmentDAO, notificationService);
    private static final IBillingService billingService = new BillingService(appointmentDAO, billDAO, dentistDAO, treatmentTypeDAO);
    private static final ReportFactory reportFactory = new ReportFactory(appointmentDAO, billDAO);
    private static final IAdminService adminService = new AdminService(staffDAO, treatmentTypeDAO, reportFactory, authService);
    private static final IHelpService helpService = new HelpService();
    private static final ClinicFacade clinicFacade = new ClinicFacade(appointmentService, billingService, authService);

    public static IAuthService getAuthService() { return authService; }
    public static IAppointmentService getAppointmentService() { return appointmentService; }
    public static IBillingService getBillingService() { return billingService; }
    public static IAdminService getAdminService() { return adminService; }
    public static IHelpService getHelpService() { return helpService; }
    public static ClinicFacade getClinicFacade() { return clinicFacade; }
    public static PatientDAO getPatientDAO() { return patientDAO; }
    public static DentistDAO getDentistDAO() { return dentistDAO; }
    public static StaffDAO getStaffDAO() { return staffDAO; }
    public static TreatmentTypeDAO getTreatmentTypeDAO() { return treatmentTypeDAO; }
}