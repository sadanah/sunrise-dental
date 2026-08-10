package com.sunrisedentalclinic.web.api;
import com.sunrisedentalclinic.web.api.ServiceFactoryApi;
import com.sunrisedentalclinic.dao.PatientDAO;
import com.sunrisedentalclinic.domain.Patient;
import com.sunrisedentalclinic.web.ApiSessionUtil;
import com.sunrisedentalclinic.web.api.dto.PatientRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/api/patients")
public class PatientApiServlet extends HttpServlet {
    private final PatientDAO patientDAO;
    public PatientApiServlet() { this(ServiceFactoryApi.getPatientDAO()); }
    public PatientApiServlet(PatientDAO d) { patientDAO = d; }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!ApiSessionUtil.isAuthenticated(req)) { JsonUtil.writeJson(res, 401, new ApiError("Unauthorized")); return; }
        String id = req.getParameter("patientID");
        if (id != null) {
            Patient p = patientDAO.findById(id);
            if (p == null) { JsonUtil.writeJson(res, 404, new ApiError("Not found")); return; }
            JsonUtil.writeJson(res, 200, p);
        } else {
            JsonUtil.writeJson(res, 200, patientDAO.findAll());
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!ApiSessionUtil.hasRole(req, "RECEPTIONIST") && !ApiSessionUtil.hasRole(req, "ADMIN")) {
            JsonUtil.writeJson(res, 403, new ApiError("Forbidden")); return;
        }
        try {
            PatientRequest r = JsonUtil.readJson(req, PatientRequest.class);
            if ("delete".equals(r.getAction())) {
                patientDAO.delete(r.getPatientID());
            } else if ("update".equals(r.getAction())) {
                Patient p = patientDAO.findById(r.getPatientID());
                if (p == null) { JsonUtil.writeJson(res, 404, new ApiError("Not found")); return; }
                p.setName(r.getName()); p.setContactNo(r.getContactNo()); p.setAddress(r.getAddress());
                p.setEmail(r.getEmail());
                patientDAO.update(p);
            } else {
                Patient p = new Patient(0, r.getName(), r.getContactNo(), r.getAddress(), r.getPatientID(), LocalDate.now());
                p.setEmail(r.getEmail());
                patientDAO.save(p);
            }
            JsonUtil.writeJson(res, 200, java.util.Map.of("status", "ok"));
        } catch (Exception e) {
            JsonUtil.writeJson(res, 400, new ApiError(e.getMessage()));
        }
    }
}
