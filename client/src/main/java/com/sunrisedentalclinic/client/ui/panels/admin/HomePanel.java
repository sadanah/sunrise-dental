package com.sunrisedentalclinic.client.ui.panels.admin;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.ui.components.QuickNavCard;
import com.sunrisedentalclinic.client.ui.components.StatCard;
import com.sunrisedentalclinic.client.ui.dashboard.AdminDashboardFrame;
import com.sunrisedentalclinic.client.ui.util.UIConstants;

import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class HomePanel extends JPanel {

    private final StatCard staffCard = new StatCard("👥", "Total Staff", UIConstants.BRAND_BLUE);
    private final StatCard treatmentsCard = new StatCard("🦷", "Treatments Offered", new Color(0x2E7D5B));
    private final StatCard todayApptCard = new StatCard("📅", "Today's Appointments", new Color(0xB5872F));
    private final StatCard revenueCard = new StatCard("💰", "Recent Revenue", new Color(0x8E3B46));

    public HomePanel(ApiClient apiClient, Consumer<String> navigate) {
        setLayout(new BorderLayout());
        //setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome back, Admin");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(welcome);

        JLabel subtitle = new JLabel("Here's what's happening at the clinic today.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsRow.add(staffCard);
        statsRow.add(treatmentsCard);
        statsRow.add(todayApptCard);
        statsRow.add(revenueCard);
        content.add(statsRow);
        content.add(Box.createRigidArea(new Dimension(0, 32)));

        JLabel quickNavHeader = new JLabel("Quick Actions");
        quickNavHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        quickNavHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(quickNavHeader);
        content.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel navRow = new JPanel(new GridLayout(1, 3, 16, 0));
        navRow.setOpaque(false);
        navRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        navRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        navRow.add(new QuickNavCard("🦷", "Manage Treatments", "Add, edit, or remove treatments",
                UIConstants.BRAND_BLUE, AdminDashboardFrame.MANAGE_TREATMENTS, navigate));
        navRow.add(new QuickNavCard("👥", "Manage Staff", "View and edit staff records",
                new Color(0x2E7D5B), AdminDashboardFrame.MANAGE_STAFF, navigate));
        navRow.add(new QuickNavCard("📊", "Generate Report", "Daily, staff, or revenue reports",
                new Color(0xB5872F), AdminDashboardFrame.GENERATE_REPORT, navigate));
        content.add(navRow);

        add(content, BorderLayout.NORTH);
        loadStats(apiClient);
    }

    /**
     * Loads stats off the EDT so the dashboard doesn't freeze on network calls.
     * Reuses the same generateReport(type, startDate, endDate, dentistID) call
     * GenerateReportsPanel uses — DAILY_APPOINTMENTS for today's count,
     * REVENUE with startDate == endDate == today for today's revenue.
     */
    @SuppressWarnings("unchecked")
    private void loadStats(ApiClient apiClient) {
        String today = LocalDate.now().toString(); // yyyy-MM-dd, matches server format

        new SwingWorker<Void, Void>() {
            private int staffCount = -1, treatmentCount = -1, todayApptCount = -1;
            private String revenueText = "N/A";

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<com.sunrisedentalclinic.client.dto.StaffDto[]> staffResp = apiClient.getStaff();
                    if (staffResp.statusCode == 200 && staffResp.body != null) {
                        staffCount = staffResp.body.length;
                    }
                } catch (Exception ignored) { }

                try {
                    ApiClient.ApiResponse<com.sunrisedentalclinic.client.dto.TreatmentDto[]> treatResp = apiClient.getTreatments();
                    if (treatResp.statusCode == 200 && treatResp.body != null) {
                        treatmentCount = treatResp.body.length;
                    }
                } catch (Exception ignored) { }

                try {
                    ApiClient.ApiResponse<Map> apptResp =
                            apiClient.generateReport("DAILY_APPOINTMENTS", today, null, null);
                    if (apptResp.statusCode == 200 && apptResp.body != null) {
                        Object appointmentsObj = apptResp.body.get("appointments");
                        if (appointmentsObj instanceof List) {
                            todayApptCount = ((List<?>) appointmentsObj).size();
                        }
                    }
                } catch (Exception ignored) { }

                try {
                    ApiClient.ApiResponse<Map> revenueResp =
                            apiClient.generateReport("REVENUE", today, today, null);
                    if (revenueResp.statusCode == 200 && revenueResp.body != null) {
                        Object total = revenueResp.body.get("totalRevenue");
                        if (total != null) revenueText = "$" + total;
                    }
                } catch (Exception ignored) { }

                return null;
            }

            @Override
            protected void done() {
                staffCard.setValue(staffCount >= 0 ? String.valueOf(staffCount) : "N/A");
                treatmentsCard.setValue(treatmentCount >= 0 ? String.valueOf(treatmentCount) : "N/A");
                todayApptCard.setValue(todayApptCount >= 0 ? String.valueOf(todayApptCount) : "N/A");
                revenueCard.setValue(revenueText);
            }
        }.execute();
    }
}