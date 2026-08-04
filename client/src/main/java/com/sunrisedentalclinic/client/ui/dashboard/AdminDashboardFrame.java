package com.sunrisedentalclinic.client.ui.dashboard;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.AppSession;
import com.sunrisedentalclinic.client.ui.LoginFrame;
import com.sunrisedentalclinic.client.ui.components.SidebarPanel;
import com.sunrisedentalclinic.client.ui.panels.admin.HomePanel;
import com.sunrisedentalclinic.client.ui.panels.admin.ManageTreatmentsPanel;
import com.sunrisedentalclinic.client.ui.panels.admin.ManageStaffPanel;
import com.sunrisedentalclinic.client.ui.util.IconLoader;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {

    private static final String HOME = "HOME";
    private static final String MANAGE_TREATMENTS = "MANAGE_TREATMENTS";
    private static final String MANAGE_STAFF = "MANAGE_STAFF";

    private final ApiClient apiClient;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public AdminDashboardFrame(ApiClient apiClient) {
        super("Sunrise Dental Clinic — Admin");
        this.apiClient = apiClient;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        SidebarPanel sidebar = new SidebarPanel(this::navigate);
        sidebar.addNavButton("Dashboard", IconLoader.load("home.png"), HOME, this::navigate);
        sidebar.addSection("Treatments", IconLoader.load("treatment.png"));
        sidebar.addNavButton("Manage", null, MANAGE_TREATMENTS, this::navigate);
        sidebar.addSection("Staff", IconLoader.load("staff.png"));
        sidebar.addNavButton("Manage", null, MANAGE_STAFF, this::navigate);
        sidebar.addGlue();
        sidebar.addNavButton("Logout", IconLoader.load("logout.png"), "LOGOUT", this::navigate);

        contentPanel.add(new HomePanel(), HOME);
        contentPanel.add(new ManageTreatmentsPanel(apiClient), MANAGE_TREATMENTS);
        contentPanel.add(new ManageStaffPanel(apiClient), MANAGE_STAFF);

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