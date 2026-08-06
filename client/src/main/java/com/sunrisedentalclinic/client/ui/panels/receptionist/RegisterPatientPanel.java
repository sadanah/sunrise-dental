package com.sunrisedentalclinic.client.ui.panels.receptionist;

import com.sunrisedentalclinic.client.ApiClient;

import javax.swing.*;
import java.awt.*;

public class RegisterPatientPanel extends JPanel {

    private final JTextField nameField = new JTextField(20);
    private final JTextField idField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField addressField = new JTextField(20);

    private final JLabel statusLabel = new JLabel(" ");
    private final JButton registerButton = new JButton("Register Patient");

    private final ApiClient apiClient;

    /**
     * Front-end only email validation.
     * This field is intentionally NOT sent to the server.
     */
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public RegisterPatientPanel(ApiClient apiClient) {
        this.apiClient = apiClient;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));

        form.add(new JLabel("Name:"));
        form.add(nameField);

        form.add(new JLabel("Patient ID:"));
        form.add(idField);

        form.add(new JLabel("Contact No:"));
        form.add(contactField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Address:"));
        form.add(addressField);

        add(form, BorderLayout.NORTH);

        statusLabel.setForeground(Color.RED);

        JPanel south = new JPanel(new BorderLayout());
        south.add(statusLabel, BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonRow.add(registerButton);
        south.add(buttonRow, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> registerPatient());
    }

    private void registerPatient() {

        String name = nameField.getText().trim();
        String id = idField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty()
                || id.isEmpty()
                || contact.isEmpty()
                || email.isEmpty()
                || address.isEmpty()) {

            statusLabel.setForeground(Color.RED);
            statusLabel.setText("All fields are required.");
            return;
        }

        // Front-end only email validation
        if (!email.matches(EMAIL_PATTERN)) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please enter a valid email address.");
            emailField.requestFocusInWindow();
            return;
        }

        registerButton.setEnabled(false);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Submitting...");

        new SwingWorker<Void, Void>() {

            int statusCode;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    // Email is intentionally NOT sent to the backend.
                    ApiClient.ApiResponse<Void> resp =
                            apiClient.savePatient(
                                    "create",
                                    id,
                                    name,
                                    contact,
                                    address);

                    statusCode = resp.statusCode;
                    errorMessage = resp.errorMessage;

                } catch (Exception ex) {
                    statusCode = -1;
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }

                return null;
            }

            @Override
            protected void done() {

                registerButton.setEnabled(true);

                if (statusCode >= 200 && statusCode < 300) {

                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText("Patient registered.");

                    nameField.setText("");
                    idField.setText("");
                    contactField.setText("");
                    emailField.setText("");
                    addressField.setText("");

                } else {

                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(
                            errorMessage != null
                                    ? errorMessage
                                    : "Registration failed (status " + statusCode + ")."
                    );
                }
            }
        }.execute();
    }
}