package com.sunrisedentalclinic.client.ui.panels.receptionist;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.dto.AppointmentDto;
import com.sunrisedentalclinic.client.dto.PatientDto;
import com.sunrisedentalclinic.client.ui.components.QuickNavCard;
import com.sunrisedentalclinic.client.ui.components.StatCard;
import com.sunrisedentalclinic.client.ui.dashboard.ReceptionistDashboardFrame;
import com.sunrisedentalclinic.client.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.function.Consumer;

public class HomePanel extends JPanel {

    private final StatCard todayApptCard = new StatCard("📅", "Today's Appointments", UIConstants.BRAND_BLUE);
    private final StatCard totalPatientsCard = new StatCard("🧑‍🤝‍🧑", "Total Patients", new Color(0x2E7D5B));
    private final StatCard scheduledCard = new StatCard("✅", "Scheduled", new Color(0xB5872F));
    private final StatCard cancelledCard = new StatCard("❌", "Cancelled", new Color(0x8E3B46));

    public HomePanel(ApiClient apiClient, Consumer<String> navigate) {
        setLayout(new BorderLayout());
        //setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(welcome);

        JLabel subtitle = new JLabel("Here's what's happening at the front desk today.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsRow.add(todayApptCard);
        statsRow.add(totalPatientsCard);
        statsRow.add(scheduledCard);
        statsRow.add(cancelledCard);
        content.add(statsRow);
        content.add(Box.createRigidArea(new Dimension(0, 32)));

        JLabel quickNavHeader = new JLabel("Quick Actions");
        quickNavHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        quickNavHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(quickNavHeader);
        content.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel navGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        navGrid.setOpaque(false);
        navGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        navGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        navGrid.add(new QuickNavCard("📅", "Register Appointment", "Book a new appointment",
                UIConstants.BRAND_BLUE, ReceptionistDashboardFrame.REGISTER_APPOINTMENT, navigate));
        navGrid.add(new QuickNavCard("🔍", "Search Appointment", "Find, view, or cancel",
                new Color(0x2E7D5B), ReceptionistDashboardFrame.SEARCH_APPOINTMENT, navigate));
        navGrid.add(new QuickNavCard("🧾", "Generate Bill", "Create a bill for a patient",
                new Color(0xB5872F), ReceptionistDashboardFrame.GENERATE_BILL, navigate));
        navGrid.add(new QuickNavCard("➕", "Register Patient", "Add a new patient record",
                new Color(0x8E3B46), ReceptionistDashboardFrame.REGISTER_PATIENT, navigate));
        navGrid.add(new QuickNavCard("🧑‍🤝‍🧑", "Search Patient", "Find or manage patients",
                new Color(0x4A5A8C), ReceptionistDashboardFrame.SEARCH_PATIENT, navigate));

        JPanel emptySlot = new JPanel();
        emptySlot.setOpaque(false);
        navGrid.add(emptySlot);

        content.add(navGrid);

        add(content, BorderLayout.NORTH);
        loadStats(apiClient);
    }

    private void loadStats(ApiClient apiClient) {
        String today = LocalDate.now().toString(); // yyyy-MM-dd

        new SwingWorker<Void, Void>() {
            private int todayCount = -1, scheduledCount = -1, cancelledCount = -1, patientCount = -1;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<AppointmentDto[]> apptResp = apiClient.getAllAppointments();
                    if (apptResp.statusCode == 200 && apptResp.body != null) {
                        todayCount = 0;
                        scheduledCount = 0;
                        cancelledCount = 0;
                        for (AppointmentDto a : apptResp.body) {
                            if (today.equals(a.getAppointmentDate())) todayCount++;
                            if ("SCHEDULED".equals(a.getStatus())) scheduledCount++;
                            if ("CANCELLED".equals(a.getStatus())) cancelledCount++;
                        }
                    }
                } catch (Exception ignored) { }

                try {
                    ApiClient.ApiResponse<PatientDto[]> patientResp = apiClient.getPatients();
                    if (patientResp.statusCode == 200 && patientResp.body != null) {
                        patientCount = patientResp.body.length;
                    }
                } catch (Exception ignored) { }

                return null;
            }

            @Override
            protected void done() {
                todayApptCard.setValue(todayCount >= 0 ? String.valueOf(todayCount) : "N/A");
                totalPatientsCard.setValue(patientCount >= 0 ? String.valueOf(patientCount) : "N/A");
                scheduledCard.setValue(scheduledCount >= 0 ? String.valueOf(scheduledCount) : "N/A");
                cancelledCard.setValue(cancelledCount >= 0 ? String.valueOf(cancelledCount) : "N/A");
            }
        }.execute();
    }
}