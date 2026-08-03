package com.sunrisedentalclinic.web.api;

import com.sunrisedentalclinic.web.api.ServiceFactoryApi;
import com.sunrisedentalclinic.dao.TreatmentTypeDAO;
import com.sunrisedentalclinic.domain.TreatmentType;
import com.sunrisedentalclinic.service.IAdminService;
import com.sunrisedentalclinic.web.ApiSessionUtil;
import com.sunrisedentalclinic.web.api.dto.TreatmentRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/api/admin/treatments")
public class TreatmentApiServlet extends HttpServlet {
    private final IAdminService adminService;
    private final TreatmentTypeDAO treatmentTypeDAO;
    public TreatmentApiServlet() { this(ServiceFactoryApi.getAdminService(), ServiceFactoryApi.getTreatmentTypeDAO()); }
    public TreatmentApiServlet(IAdminService a, TreatmentTypeDAO d) { adminService = a; treatmentTypeDAO = d; }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!ApiSessionUtil.hasRole(req, "ADMIN") && !ApiSessionUtil.hasRole(req, "RECEPTIONIST")) {
            JsonUtil.writeJson(res, 403, new ApiError("Forbidden"));
            return;
        }
        JsonUtil.writeJson(res, 200, treatmentTypeDAO.findAll());
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!ApiSessionUtil.hasRole(req, "ADMIN")) { JsonUtil.writeJson(res, 403, new ApiError("Forbidden")); return; }
        try {
            TreatmentRequest r = JsonUtil.readJson(req, TreatmentRequest.class);
            if ("delete".equals(r.getAction())) {
                adminService.manageTreatment("delete", new TreatmentType(r.getTreatmentID(), "", BigDecimal.ZERO));
            } else {
                adminService.manageTreatment(r.getAction(), new TreatmentType(r.getTreatmentID(), r.getTreatmentName(), new BigDecimal(r.getBaseCost())));
            }
            JsonUtil.writeJson(res, 200, java.util.Map.of("status", "ok"));
        } catch (Exception e) {
            JsonUtil.writeJson(res, 400, new ApiError(e.getMessage()));
        }
    }
}
