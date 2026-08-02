package com.sunrisedentalclinic.web.api;

import com.sunrisedentalclinic.domain.Session;
import com.sunrisedentalclinic.exception.AuthenticationException;
import com.sunrisedentalclinic.service.IAuthService;
import com.sunrisedentalclinic.web.ServiceFactory;
import com.sunrisedentalclinic.web.api.dto.LoginRequest;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/login")
public class AuthApiServlet extends HttpServlet {

    private final IAuthService authService;

    public AuthApiServlet() {
        this(ServiceFactory.getAuthService());
    }

    public AuthApiServlet(IAuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            LoginRequest loginRequest = JsonUtil.readJson(request, LoginRequest.class);
            Session session = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

            HttpSession httpSession = request.getSession(true);
            httpSession.setAttribute("appSession", session);
            httpSession.setMaxInactiveInterval(30 * 60);

            JsonUtil.writeJson(response, 200, session);
        } catch (AuthenticationException e) {
            JsonUtil.writeJson(response, 401, new ApiError(e.getMessage()));
        }
    }
}