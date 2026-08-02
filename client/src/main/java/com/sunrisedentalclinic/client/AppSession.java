package com.sunrisedentalclinic.client;
import com.sunrisedentalclinic.client.dto.SessionDto;

public class AppSession {
    private static SessionDto currentSession;
    private AppSession() {}
    public static void set(SessionDto s) { currentSession = s; }
    public static SessionDto get() { return currentSession; }
    public static String getRole() { return currentSession != null ? currentSession.getRole() : null; }
    public static String getStaffID() { return currentSession != null ? currentSession.getStaffID() : null; }
    public static void clear() { currentSession = null; }
    public static boolean isLoggedIn() { return currentSession != null; }
}