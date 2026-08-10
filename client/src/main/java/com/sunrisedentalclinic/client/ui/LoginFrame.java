package com.sunrisedentalclinic.client.ui;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.AppSession;
import com.sunrisedentalclinic.client.dto.SessionDto;
import com.sunrisedentalclinic.client.ui.dashboard.AdminDashboardFrame;
import com.sunrisedentalclinic.client.ui.dashboard.DentistDashboardFrame;
import com.sunrisedentalclinic.client.ui.dashboard.ReceptionistDashboardFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import com.sunrisedentalclinic.client.ui.util.UIConstants;
import java.io.IOException;
import java.net.URL;

public class LoginFrame extends JFrame {

    private static final Color BRAND_BG = UIConstants.BRAND_BLUE;      // #274671
    private static final Color BRAND_ACCENT = UIConstants.BRAND_BLUE;  // login button matches panel
    private static final Color BRAND_TEXT = UIConstants.BRAND_TEXT;    // white

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JLabel errorLabel = new JLabel(" ");
    private final ApiClient apiClient;

    public LoginFrame(ApiClient apiClient) {
        super("Sunrise Dental Clinic: Login");
        this.apiClient = apiClient;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        add(buildBrandingPanel(), BorderLayout.WEST);
        add(buildLoginPanel(), BorderLayout.CENTER);
    }

    // ===== LEFT: Branding panel =====
    private JPanel buildBrandingPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(420, 0));
        panel.setBackground(BRAND_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        panel.add(Box.createVerticalGlue());

        JLabel logo = new JLabel(loadLogoIcon());
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logo);
        panel.add(Box.createRigidArea(new Dimension(0, 24)));

        JLabel clinicName = new JLabel("SUNRISE DENTAL CLINIC");
        clinicName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        clinicName.setForeground(BRAND_TEXT);
        clinicName.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(clinicName);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel tagline = new JLabel("Where every smile shines.");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        tagline.setForeground(new Color(0xD6E0EE));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(tagline);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private ImageIcon loadLogoIcon() {
        try {
            URL url = getClass().getResource("/icons/sunrise_logo.png");
            if (url == null) throw new IOException("Logo resource not found");
            ImageIcon raw = new ImageIcon(url);
            Image scaled = raw.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            // Fallback: simple placeholder circle so layout never breaks if the asset is missing
            BufferedImage placeholder = new BufferedImage(140, 140, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = placeholder.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(BRAND_ACCENT);
            g.fillOval(10, 10, 120, 120);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, 48));
            g.drawString("S", 55, 90);
            g.dispose();
            return new ImageIcon(placeholder);
        }
    }

    // ===== RIGHT: Login form panel =====
    private JPanel buildLoginPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        //outer.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        //form.setBackground(Color.WHITE);
        //form.setPreferredSize(new Dimension(580, 650));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel title = new JLabel("Staff Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        form.add(title, gbc);

        gbc.gridy = 1;
        JLabel subtitle = new JLabel("Sign in to access your dashboard");
        subtitle.setForeground(Color.GRAY);
        form.add(subtitle, gbc);
        ((GridBagLayout) form.getLayout()).setConstraints(subtitle, withInsets(gbc, 8, 0, 24, 0));

        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 0, 2, 0);
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        form.add(new JLabel("Username"), gbc);

        gbc.gridy = 3;
        usernameField.setPreferredSize(new Dimension(0, 34));
        form.add(usernameField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(14, 0, 2, 0);
        form.add(new JLabel("Password"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(6, 0, 2, 0);
        passwordField.setPreferredSize(new Dimension(0, 34));
        form.add(passwordField, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(10, 0, 0, 0);
        errorLabel.setForeground(Color.RED);
        form.add(errorLabel, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(20, 0, 0, 0);
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(0, 38));
        loginButton.setBackground(BRAND_ACCENT);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        form.add(loginButton, gbc);

        outer.add(form);

        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());

        return outer;
    }

    private GridBagConstraints withInsets(GridBagConstraints base, int top, int left, int bottom, int right) {
        GridBagConstraints copy = (GridBagConstraints) base.clone();
        copy.insets = new Insets(top, left, bottom, right);
        return copy;
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