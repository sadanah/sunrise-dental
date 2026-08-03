package com.sunrisedentalclinic.client;

import com.sunrisedentalclinic.client.dto.SessionDto;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JLabel errorLabel = new JLabel(" ");
    private final ApiClient apiClient;

    public LoginFrame(ApiClient apiClient) {
        super("Sunrise Dental Clinic — Login");
        this.apiClient = apiClient;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 260);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("Staff Login", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(errorLabel, gbc);

        gbc.gridy = 4;
        JButton loginButton = new JButton("Login");
        panel.add(loginButton, gbc);

        add(panel);

        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        errorLabel.setText(" ");

        try {
            ApiClient.ApiResponse<SessionDto> result = apiClient.login(username, password);

            if (result.statusCode == 200 && result.body != null) {
                AppSession.set(result.body);
                openDashboardForRole(AppSession.getRole());
                dispose();
            } else {
                errorLabel.setText(result.errorMessage != null ? result.errorMessage : "Invalid username or password.");
            }
        } catch (Exception ex) {
            errorLabel.setText("Could not reach server: " + ex.getMessage());
        }
    }

    private void openDashboardForRole(String role) {
        JFrame dashboard;
        switch (role) {
            case "ADMIN":
                dashboard = new AdminDashboardFrame(apiClient);
                break;
            case "DENTIST":
                dashboard = new DentistDashboardFrame(apiClient);
                break;
            default:
                dashboard = new ReceptionistDashboardFrame(apiClient);
                break;
        }
        dashboard.setVisible(true);
    }
}