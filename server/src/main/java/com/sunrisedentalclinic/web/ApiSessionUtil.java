package com.sunrisedentalclinic.web;

import com.sunrisedentalclinic.domain.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ApiSessionUtil {

    private ApiSessionUtil() {}

    public static Session getCurrentSession(HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null) return null;
        Session appSession = (Session) httpSession.getAttribute("appSession");
        if (appSession == null || !appSession.isValid()) return null;
        return appSession;
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        return getCurrentSession(request) != null;
    }

    public static boolean hasRole(HttpServletRequest request, String role) {
        Session session = getCurrentSession(request);
        return session != null && role.equals(session.getRole());
    }
}