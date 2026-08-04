package com.sunrisedentalclinic.web.api;

import com.sunrisedentalclinic.domain.Bill;
import com.sunrisedentalclinic.exception.AppointmentNotFoundException;
import com.sunrisedentalclinic.service.IBillingService;
import com.sunrisedentalclinic.service.impl.ClinicFacade;
import com.sunrisedentalclinic.web.ApiSessionUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@WebServlet("/api/bills")
public class BillingApiServlet extends HttpServlet {

    private final ClinicFacade clinicFacade;
    private final IBillingService billingService;

    public BillingApiServlet() {
        this(ServiceFactoryApi.getClinicFacade(), ServiceFactoryApi.getBillingService());
    }

    public BillingApiServlet(ClinicFacade clinicFacade, IBillingService billingService) {
        this.clinicFacade = clinicFacade;
        this.billingService = billingService;
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
            String discountParam = body.get("discountPercent");

            Bill bill = clinicFacade.generateBill(appointmentNo);

            if (discountParam != null && !discountParam.isBlank()) {
                BigDecimal discountPercent = new BigDecimal(discountParam);
                if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
                    JsonUtil.writeJson(response, 400, new ApiError("Discount must be between 0 and 100"));
                    return;
                }
                bill = billingService.applyDiscount(bill, discountPercent);
            }

            JsonUtil.writeJson(response, 201, bill);
        } catch (AppointmentNotFoundException e) {
            JsonUtil.writeJson(response, 404, new ApiError(e.getMessage()));
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(response, 400, new ApiError("Invalid discount value"));
        } catch (Exception e) {
            JsonUtil.writeJson(response, 400, new ApiError("Invalid request: " + e.getMessage()));
        }
    }
}