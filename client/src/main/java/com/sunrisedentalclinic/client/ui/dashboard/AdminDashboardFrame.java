package com.sunrisedentalclinic.client.ui.dashboard;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.AppSession;
import com.sunrisedentalclinic.client.ui.LoginFrame;
import com.sunrisedentalclinic.client.ui.components.SidebarPanel;
import com.sunrisedentalclinic.client.ui.panels.admin.HomePanel;
import com.sunrisedentalclinic.client.ui.panels.HelpPanel;
import com.sunrisedentalclinic.client.ui.panels.admin.ManageTreatmentsPanel;
import com.sunrisedentalclinic.client.ui.panels.admin.ManageStaffPanel;
import com.sunrisedentalclinic.client.ui.panels.admin.GenerateReportsPanel;
import com.sunrisedentalclinic.client.ui.util.IconLoader;
import com.sunrisedentalclinic.client.ui.util.EmojiIcon;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {

    public static final String HOME = "HOME";
    public static final String MANAGE_TREATMENTS = "MANAGE_TREATMENTS";
    public static final String MANAGE_STAFF = "MANAGE_STAFF";
    public static final String GENERATE_REPORT = "GENERATE_REPORT";
    public static final String HELP = "HELP";

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
        sidebar.addNavButton("Dashboard", new EmojiIcon("🏠"), HOME, this::navigate);
        sidebar.addSection("  Treatments", new EmojiIcon("🦷"));
        sidebar.addNavButton("Manage Treatment", null, MANAGE_TREATMENTS, this::navigate);
        sidebar.addSection("  Staff", new EmojiIcon("👥"));
        sidebar.addNavButton("Manage Staff", null, MANAGE_STAFF, this::navigate);
        sidebar.addSection("  Reports", new EmojiIcon("📊"));
        sidebar.addNavButton("Generate Report", null, GENERATE_REPORT, this::navigate);
        sidebar.addGlue();
        sidebar.addNavButton("  Help", new EmojiIcon("❓"), HELP, this::navigate);
        sidebar.addNavButton("  Logout", new EmojiIcon("🚪"), "LOGOUT", this::navigate);

        contentPanel.add(new HomePanel(apiClient, this::navigate), HOME);
        contentPanel.add(new ManageTreatmentsPanel(apiClient), MANAGE_TREATMENTS);
        contentPanel.add(new ManageStaffPanel(apiClient), MANAGE_STAFF);
        contentPanel.add(new GenerateReportsPanel(apiClient), GENERATE_REPORT);
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