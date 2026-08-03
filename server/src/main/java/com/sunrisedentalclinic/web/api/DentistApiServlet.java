package com.sunrisedentalclinic.web.api;

import com.sunrisedentalclinic.dao.DentistDAO;
import com.sunrisedentalclinic.web.ApiSessionUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/dentists")
public class DentistApiServlet extends HttpServlet {

    private final DentistDAO dentistDAO;

    public DentistApiServlet() {
        this(ServiceFactoryApi.getDentistDAO());
    }

    public DentistApiServlet(DentistDAO dentistDAO) {
        this.dentistDAO = dentistDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!ApiSessionUtil.isAuthenticated(req)) {
            JsonUtil.writeJson(res, 401, new ApiError("Unauthorized"));
            return;
        }
        JsonUtil.writeJson(res, 200, dentistDAO.findAll());
    }
}