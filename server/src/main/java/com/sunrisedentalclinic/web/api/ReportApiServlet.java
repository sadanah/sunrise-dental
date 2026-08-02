package com.sunrisedentalclinic.web.api;
import com.sunrisedentalclinic.exception.AccessDeniedException;
import com.sunrisedentalclinic.report.Report;
import com.sunrisedentalclinic.service.IAdminService;
import com.sunrisedentalclinic.web.ApiSessionUtil;
import com.sunrisedentalclinic.web.api.dto.ReportRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/api/admin/reports")
public class ReportApiServlet extends HttpServlet {
    private final IAdminService adminService;
    public ReportApiServlet() { this(ServiceFactoryApi.getAdminService()); }
    public ReportApiServlet(IAdminService a) { adminService = a; }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String sessionID = ApiSessionUtil.getCurrentSession(req) != null ? ApiSessionUtil.getCurrentSession(req).getSessionID() : null;
        try {
            ReportRequest r = JsonUtil.readJson(req, ReportRequest.class);
            LocalDate start = r.getStartDate() != null && !r.getStartDate().isEmpty() ? LocalDate.parse(r.getStartDate()) : null;
            LocalDate end = r.getEndDate() != null && !r.getEndDate().isEmpty() ? LocalDate.parse(r.getEndDate()) : null;
            Report report = adminService.generateReport(r.getType(), sessionID, start, end, r.getDentistID());
            JsonUtil.writeJson(res, 200, report);
        } catch (AccessDeniedException e) {
            JsonUtil.writeJson(res, 403, new ApiError(e.getMessage()));
        } catch (Exception e) {
            JsonUtil.writeJson(res, 400, new ApiError(e.getMessage()));
        }
    }
}