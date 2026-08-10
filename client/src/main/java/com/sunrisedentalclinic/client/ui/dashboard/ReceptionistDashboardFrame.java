package com.sunrisedentalclinic.client.ui.dashboard;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.AppSession;
import com.sunrisedentalclinic.client.ui.LoginFrame;
import com.sunrisedentalclinic.client.ui.components.SidebarPanel;
import com.sunrisedentalclinic.client.ui.panels.receptionist.HomePanel;
import com.sunrisedentalclinic.client.ui.panels.HelpPanel;
import com.sunrisedentalclinic.client.ui.panels.receptionist.RegisterPatientPanel;
import com.sunrisedentalclinic.client.ui.panels.receptionist.SearchPatientsPanel;
import com.sunrisedentalclinic.client.ui.panels.receptionist.RegisterAppointmentPanel;
import com.sunrisedentalclinic.client.ui.panels.receptionist.SearchAppointmentPanel;
import com.sunrisedentalclinic.client.ui.panels.receptionist.GenerateBillPanel;
import com.sunrisedentalclinic.client.ui.util.IconLoader;
import com.sunrisedentalclinic.client.ui.util.EmojiIcon;
import javax.swing.*;
import java.awt.*;

public class ReceptionistDashboardFrame extends JFrame {

    public static final String HOME = "HOME";
    public static final String REGISTER_APPOINTMENT = "REGISTER_APPOINTMENT";
    public static final String SEARCH_APPOINTMENT = "SEARCH_APPOINTMENT";
    public static final String GENERATE_BILL = "GENERATE_BILL";
    public static final String REGISTER_PATIENT = "REGISTER_PATIENT";
    public static final String SEARCH_PATIENT = "SEARCH_PATIENT";
    public static final String HELP = "HELP";

    private final ApiClient apiClient;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public ReceptionistDashboardFrame(ApiClient apiClient) {
        super("Sunrise Dental Clinic — Receptionist");
        this.apiClient = apiClient;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        SidebarPanel sidebar = new SidebarPanel(this::navigate);
        sidebar.addNavButton("  Dashboard", new EmojiIcon("🏠"), HOME, this::navigate);
        sidebar.addSection("  Appointments", new EmojiIcon("📅"));
        sidebar.addNavButton("Register", null, REGISTER_APPOINTMENT, this::navigate);
        sidebar.addNavButton("Search", null, SEARCH_APPOINTMENT, this::navigate);
        sidebar.addNavButton("Generate Bill", null, GENERATE_BILL, this::navigate);
        sidebar.addSection("  Patients", new EmojiIcon("🧑‍🤝‍🧑"));
        sidebar.addNavButton("Register", null, REGISTER_PATIENT, this::navigate);
        sidebar.addNavButton("Search", null, SEARCH_PATIENT, this::navigate);
        sidebar.addGlue();
        sidebar.addNavButton("  Help", new EmojiIcon("❓"), HELP, this::navigate);
        sidebar.addNavButton("  Logout", new EmojiIcon("🚪"), "LOGOUT", this::navigate);

        contentPanel.add(new HomePanel(apiClient, this::navigate), HOME);
        contentPanel.add(new RegisterAppointmentPanel(apiClient), REGISTER_APPOINTMENT);
        contentPanel.add(new SearchAppointmentPanel(apiClient), SEARCH_APPOINTMENT);
        contentPanel.add(new GenerateBillPanel(apiClient), GENERATE_BILL);
        contentPanel.add(new RegisterPatientPanel(apiClient), REGISTER_PATIENT);
        contentPanel.add(new SearchPatientsPanel(apiClient), SEARCH_PATIENT);
        contentPanel.add(new HelpPanel(apiClient), HELP);

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