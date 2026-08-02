package com.sunrisedentalclinic.web.api;
import com.sunrisedentalclinic.service.IHelpService;
import com.sunrisedentalclinic.web.ApiSessionUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/help")
public class HelpApiServlet extends HttpServlet {
    private final IHelpService helpService;
    public HelpApiServlet() { this(ServiceFactoryApi.getHelpService()); }
    public HelpApiServlet(IHelpService h) { helpService = h; }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!ApiSessionUtil.isAuthenticated(req)) { JsonUtil.writeJson(res, 401, new ApiError("Unauthorized")); return; }
        String topic = req.getParameter("topic");
        if (topic == null) { JsonUtil.writeJson(res, 200, helpService.listHelpTopics()); return; }
        JsonUtil.writeJson(res, 200, java.util.Map.of("content", helpService.displayHelp(topic)));
    }
}