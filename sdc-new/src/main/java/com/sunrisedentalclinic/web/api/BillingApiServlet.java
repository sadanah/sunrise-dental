package com.sunrisedentalclinic.web.api;

import com.sunrisedentalclinic.domain.Bill;
import com.sunrisedentalclinic.exception.AppointmentNotFoundException;
import com.sunrisedentalclinic.service.impl.ClinicFacade;
import com.sunrisedentalclinic.web.ApiSessionUtil;
import com.sunrisedentalclinic.web.ServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/bills")
public class BillingApiServlet extends HttpServlet {

    private final ClinicFacade clinicFacade;

    public BillingApiServlet() {
        this(ServiceFactory.getClinicFacade());
    }

    public BillingApiServlet(ClinicFacade clinicFacade) {
        this.clinicFacade = clinicFacade;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!ApiSessionUtil.hasRole(request, "RECEPTIONIST")) {
            JsonUtil.writeJson(response, 403, new ApiError("Forbidden: Receptionist role required"));
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, String> body = JsonUtil.readJson(request, Map.class);
            String appointmentNo = body.get("appointmentNo");

            Bill bill = clinicFacade.generateBill(appointmentNo);
            JsonUtil.writeJson(response, 201, bill);
        } catch (AppointmentNotFoundException e) {
            JsonUtil.writeJson(response, 404, new ApiError(e.getMessage()));
        } catch (Exception e) {
            JsonUtil.writeJson(response, 400, new ApiError("Invalid request: " + e.getMessage()));
        }
    }
}