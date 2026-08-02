package com.sunrisedentalclinic.client;

import javax.swing.*;
import java.awt.*;

public class ReceptionistDashboardFrame extends JFrame {

    private final ApiClient apiClient = new ApiClient();

    public ReceptionistDashboardFrame() {
        super("Receptionist Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcome = new JLabel("Welcome, " + AppSession.getStaffID() + " (Receptionist)");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 15f));
        panel.add(welcome, BorderLayout.NORTH);

        JLabel placeholder = new JLabel("Appointment / Patient / Billing workflows coming soon.", SwingConstants.CENTER);
        panel.add(placeholder, BorderLayout.CENTER);

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
            // Server unreachable or session already gone - not fatal, still log out locally
            System.out.println("[WARN] Logout call failed: " + ex.getMessage());
        }
        AppSession.clear();
        dispose();
        new LoginFrame().setVisible(true);
    }
}