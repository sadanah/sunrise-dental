package com.sunrisedentalclinic.client.ui.dashboard;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.AppSession;
import com.sunrisedentalclinic.client.ui.LoginFrame;
import com.sunrisedentalclinic.client.ui.components.SidebarPanel;
import com.sunrisedentalclinic.client.ui.panels.dentist.HomePanel;
import com.sunrisedentalclinic.client.ui.panels.dentist.DentistAppointmentsPanel;
import com.sunrisedentalclinic.client.ui.panels.dentist.DentistPatientsPanel;
import com.sunrisedentalclinic.client.ui.util.IconLoader;

import javax.swing.*;
import java.awt.*;

public class DentistDashboardFrame extends JFrame {

    private static final String HOME = "HOME";
    private static final String APPOINTMENTS = "APPOINTMENTS";
    private static final String PATIENTS = "PATIENTS";

    private final ApiClient apiClient;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public DentistDashboardFrame(ApiClient apiClient) {
        super("Sunrise Dental Clinic — Dentist");
        this.apiClient = apiClient;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        SidebarPanel sidebar = new SidebarPanel(this::navigate);
        sidebar.addNavButton("Dashboard", IconLoader.load("home.png"), HOME, this::navigate);
        sidebar.addSection("My Appointments", IconLoader.load("appointment.png"));
        sidebar.addNavButton("View / Search", null, APPOINTMENTS, this::navigate);
        sidebar.addSection("My Patients", IconLoader.load("patient.png"));
        sidebar.addNavButton("View", null, PATIENTS, this::navigate);
        sidebar.addGlue();
        sidebar.addNavButton("Logout", IconLoader.load("logout.png"), "LOGOUT", this::navigate);

        contentPanel.add(new HomePanel(), HOME);
        contentPanel.add(new DentistAppointmentsPanel(apiClient), APPOINTMENTS);
        contentPanel.add(new DentistPatientsPanel(apiClient), PATIENTS);

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void navigate(String key) {
        if (key.equals("LOGOUT")) {
            logout();
            return;
        }
        cardLayout.show(contentPanel, key);
    }

    private void logout() {
        try {
            apiClient.logout();
        } catch (Exception ex) {
            System.out.println("[WARN] Logout call failed: " + ex.getMessage());
        }
        AppSession.clear();
        dispose();
        new LoginFrame(apiClient).setVisible(true);
    }
}