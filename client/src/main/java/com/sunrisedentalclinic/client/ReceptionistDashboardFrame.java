package com.sunrisedentalclinic.client;

import javax.swing.*;
import java.awt.*;

public class ReceptionistDashboardFrame extends JFrame {

    private final ApiClient apiClient;

    public ReceptionistDashboardFrame(ApiClient apiClient) {
        super("Receptionist Dashboard");
        this.apiClient = apiClient;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcome = new JLabel("Welcome, " + AppSession.getStaffID() + " (Receptionist)");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 15f));
        panel.add(welcome, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(0, 1, 8, 8));
        JButton registerAppointmentButton = new JButton("Register Appointment");
        registerAppointmentButton.addActionListener(e ->
                new RegisterAppointmentDialog(this, apiClient).setVisible(true));
        actions.add(registerAppointmentButton);
        panel.add(actions, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(logoutButton);
        panel.add(south, BorderLayout.SOUTH);

        add(panel);
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