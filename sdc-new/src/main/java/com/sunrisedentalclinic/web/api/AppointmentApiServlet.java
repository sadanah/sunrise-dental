package com.sunrisedentalclinic.web.api;

import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.exception.AppointmentNotFoundException;
import com.sunrisedentalclinic.exception.SlotUnavailableException;
import com.sunrisedentalclinic.service.impl.ClinicFacade;
import com.sunrisedentalclinic.web.ApiSessionUtil;
import com.sunrisedentalclinic.web.ServiceFactory;
import com.sunrisedentalclinic.web.api.dto.RegisterAppointmentRequest;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@WebServlet("/api/appointments")
public class AppointmentApiServlet extends HttpServlet {

    private final ClinicFacade clinicFacade;

    public AppointmentApiServlet() {
        this(ServiceFactory.getClinicFacade());
    }

    public AppointmentApiServlet(ClinicFacade clinicFacade) {
        this.clinicFacade = clinicFacade;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!ApiSessionUtil.hasRole(request, "RECEPTIONIST")) {
            JsonUtil.writeJson(response, 403, new ApiError("Forbidden: Receptionist role required"));
            return;
        }

        try {
            RegisterAppointmentRequest req = JsonUtil.readJson(request, RegisterAppointmentRequest.class);
            String staffID = ApiSessionUtil.getCurrentSession(request).getStaffID();

            Appointment appointment = clinicFacade.registerAppointment(
                    req.getPatientID(), req.getDentistID(), req.getTreatmentID(), staffID,
                    LocalDate.parse(req.getDate()), LocalTime.parse(req.getTime()));

            JsonUtil.writeJson(response, 201, appointment);
        } catch (SlotUnavailableException e) {
            JsonUtil.writeJson(response, 409, new ApiError(e.getMessage()));
        } catch (Exception e) {
            JsonUtil.writeJson(response, 400, new ApiError("Invalid request: " + e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!ApiSessionUtil.isAuthenticated(request)) {
            JsonUtil.writeJson(response, 401, new ApiError("Unauthorized"));
            return;
        }

        String appointmentNo = request.getParameter("appointmentNo");
        if (appointmentNo == null || appointmentNo.isBlank()) {
            JsonUtil.writeJson(response, 400, new ApiError("appointmentNo query parameter is required"));
            return;
        }

        try {
            Appointment appointment = clinicFacade.searchAppointment(appointmentNo);
            JsonUtil.writeJson(response, 200, appointment);
        } catch (AppointmentNotFoundException e) {
            JsonUtil.writeJson(response, 404, new ApiError(e.getMessage()));
        }
    }
}