package com.sunrisedentalclinic.web.api;
import com.sunrisedentalclinic.dao.StaffDAO;
import com.sunrisedentalclinic.domain.*;
import com.sunrisedentalclinic.service.IAdminService;
import com.sunrisedentalclinic.web.ApiSessionUtil;
import com.sunrisedentalclinic.web.api.dto.StaffRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/api/admin/staff")
public class StaffApiServlet extends HttpServlet {
    private final IAdminService adminService;
    private final StaffDAO staffDAO;
    public StaffApiServlet() { this(ServiceFactoryApi.getAdminService(), ServiceFactoryApi.getStaffDAO()); }
    public StaffApiServlet(IAdminService a, StaffDAO d) { adminService = a; staffDAO = d; }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!ApiSessionUtil.hasRole(req, "ADMIN")) { JsonUtil.writeJson(res, 403, new ApiError("Forbidden")); return; }
        JsonUtil.writeJson(res, 200, staffDAO.findAll());
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!ApiSessionUtil.hasRole(req, "ADMIN")) { JsonUtil.writeJson(res, 403, new ApiError("Forbidden")); return; }
        try {
            StaffRequest r = JsonUtil.readJson(req, StaffRequest.class);
            if ("delete".equals(r.getAction())) {
                Staff dummy = new Receptionist(0, "", "", "", r.getStaffID(), "", "");
                adminService.manageStaff("delete", dummy);
            } else {
                Staff staff = build(r);
                adminService.manageStaff(r.getAction(), staff);
            }
            JsonUtil.writeJson(res, 200, java.util.Map.of("status", "ok"));
        } catch (Exception e) {
            JsonUtil.writeJson(res, 400, new ApiError(e.getMessage()));
        }
    }

    private Staff build(StaffRequest r) {
        String hash = com.sunrisedentalclinic.util.PasswordUtil.hash(r.getPassword());
        if ("DENTIST".equals(r.getRole()))
            return new Dentist(0, r.getName(), r.getContactNo(), r.getAddress(), r.getStaffID(), r.getUsername(), hash, r.getSpecialization(), new BigDecimal(r.getConsultationFee()));
        if ("ADMIN".equals(r.getRole()))
            return new Admin(0, r.getName(), r.getContactNo(), r.getAddress(), r.getStaffID(), r.getUsername(), hash);
        return new Receptionist(0, r.getName(), r.getContactNo(), r.getAddress(), r.getStaffID(), r.getUsername(), hash);
    }
}