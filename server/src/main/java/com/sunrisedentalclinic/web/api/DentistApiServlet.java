package com.sunrisedentalclinic.web.api;

import com.sunrisedentalclinic.dao.DentistDAO;
import com.sunrisedentalclinic.domain.Dentist;
import com.sunrisedentalclinic.web.ApiSessionUtil;
import com.sunrisedentalclinic.web.api.ServiceFactoryApi;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.util.List;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!ApiSessionUtil.isAuthenticated(request)) {
            JsonUtil.writeJson(response, 401, new ApiError("Unauthorized"));
            return;
        }

        List<Dentist> dentists = dentistDAO.findAll();
        JsonUtil.writeJson(response, 200, dentists);
    }
}
