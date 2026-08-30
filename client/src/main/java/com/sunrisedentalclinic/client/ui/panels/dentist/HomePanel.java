package com.sunrisedentalclinic.client.ui.panels.dentist;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.AppSession;
import com.sunrisedentalclinic.client.dto.AppointmentDto;
import com.sunrisedentalclinic.client.dto.PatientDto;
import com.sunrisedentalclinic.client.ui.components.QuickNavCard;
import com.sunrisedentalclinic.client.ui.components.StatCard;
import com.sunrisedentalclinic.client.ui.dashboard.DentistDashboardFrame;
import com.sunrisedentalclinic.client.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class HomePanel extends JPanel {

    private final StatCard totalApptCard = new StatCard("📅", "My Appointments", UIConstants.BRAND_BLUE);
    private final StatCard todayApptCard = new StatCard("🕒", "Today's Appointments", new Color(0xB5872F));
    private final StatCard patientsCard = new StatCard("🧑‍🤝‍🧑", "My Patients", new Color(0x2E7D5B));

    public HomePanel(ApiClient apiClient, Consumer<String> navigate) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.CARD_BG.darker()); // matches dark dashboard background
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome, " + AppSession.getName());
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcome.setForeground(UIConstants.CARD_TEXT_PRIMARY);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(welcome);

        JLabel subtitle = new JLabel("Here's your schedule at a glance.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(UIConstants.CARD_TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(700, 100));
        statsRow.add(totalApptCard);
        statsRow.add(todayApptCard);
        statsRow.add(patientsCard);
        content.add(statsRow);
        content.add(Box.createRigidArea(new Dimension(0, 32)));

        JLabel quickNavHeader = new JLabel("Quick Actions");
        quickNavHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        quickNavHeader.setForeground(UIConstants.CARD_TEXT_PRIMARY);
        quickNavHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(quickNavHeader);
        content.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel navRow = new JPanel(new GridLayout(1, 2, 16, 0));
        navRow.setOpaque(false);
        navRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        navRow.setMaximumSize(new Dimension(460, 110));
        navRow.add(new QuickNavCard("📅", "View / Search Appointments", "See your schedule",
                UIConstants.BRAND_BLUE, DentistDashboardFrame.APPOINTMENTS, navigate));
        navRow.add(new QuickNavCard("🧑‍🤝‍🧑", "View My Patients", "Patients under your care",
                new Color(0x2E7D5B), DentistDashboardFrame.PATIENTS, navigate));
        content.add(navRow);

        add(content, BorderLayout.NORTH);
        loadStats(apiClient);
    }

    /**
     * Same scoping logic as DentistAppointmentsPanel/DentistPatientsPanel:
     * filter the full appointment list down to this dentist's own via
     * AppSession.getStaffID(), since no dentist-scoped endpoint exists.
     */
    private void loadStats(ApiClient apiClient) {
        String dentistID = AppSession.getStaffID();
        String today = LocalDate.now().toString(); // yyyy-MM-dd

        new SwingWorker<Void, Void>() {
            private int totalCount = -1, todayCount = -1, patientCount = -1;

            @Override
            protected Void doInBackground() {
                AppointmentDto[] appts = null;
                try {
                    ApiClient.ApiResponse<AppointmentDto[]> apptResp = apiClient.getAllAppointments();
                    if (apptResp.statusCode == 200 && apptResp.body != null) {
                        appts = apptResp.body;
                        totalCount = 0;
                        todayCount = 0;
                        Set<String> ownPatientIDs = new HashSet<>();
                        for (AppointmentDto a : appts) {
                            if (dentistID != null && dentistID.equals(a.getDentistID())) {
                                totalCount++;
                                if (today.equals(a.getAppointmentDate())) todayCount++;
                                ownPatientIDs.add(a.getPatientID());
                            }
                        }
                        patientCount = ownPatientIDs.size();
                    }
                } catch (Exception ignored) { }

                return null;
            }

            @Override
            protected void done() {
                totalApptCard.setValue(totalCount >= 0 ? String.valueOf(totalCount) : "N/A");
                todayApptCard.setValue(todayCount >= 0 ? String.valueOf(todayCount) : "N/A");
                patientsCard.setValue(patientCount >= 0 ? String.valueOf(patientCount) : "N/A");
            }
        }.execute();
    }
}