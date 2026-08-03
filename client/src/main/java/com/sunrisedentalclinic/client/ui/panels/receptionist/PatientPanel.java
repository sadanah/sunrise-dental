package com.sunrisedentalclinic.client.ui.panels.receptionist;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.dto.PatientDto;

import javax.swing.*;
import java.awt.*;

public class PatientPanel extends JPanel {

    private final ApiClient apiClient;

    // Register tab fields
    private final JTextField nameField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);
    private final JTextField addressField = new JTextField(20);
    private final JLabel registerStatus = new JLabel(" ");
    private final JButton registerButton = new JButton("Register Patient");

    // Search tab fields
    private final JTextField searchField = new JTextField(15);
    private final JButton searchButton = new JButton("Search");
    private final JTextArea searchResults = new JTextArea(12, 40);
    private final JLabel searchStatus = new JLabel(" ");

    public PatientPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Register", buildRegisterTab());
        tabs.addTab("Search", buildSearchTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildRegisterTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Contact No:"));
        form.add(contactField);
        form.add(new JLabel("Address:"));
        form.add(addressField);
        panel.add(form, BorderLayout.NORTH);

        registerStatus.setForeground(Color.RED);
        JPanel south = new JPanel(new BorderLayout());
        south.add(registerStatus, BorderLayout.CENTER);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonRow.add(registerButton);
        south.add(buttonRow, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> registerPatient());
        return panel;
    }

    private JPanel buildSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new JLabel("Name or Patient ID contains:"));
        searchRow.add(searchField);
        searchRow.add(searchButton);
        panel.add(searchRow, BorderLayout.NORTH);

        searchResults.setEditable(false);
        panel.add(new JScrollPane(searchResults), BorderLayout.CENTER);

        searchStatus.setForeground(Color.RED);
        panel.add(searchStatus, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchPatients());
        return panel;
    }

    private void registerPatient() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            registerStatus.setForeground(Color.RED);
            registerStatus.setText("All fields are required.");
            return;
        }

        registerButton.setEnabled(false);
        registerStatus.setForeground(Color.DARK_GRAY);
        registerStatus.setText("Submitting...");

        new SwingWorker<Void, Void>() {
            int statusCode;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    // ASSUMPTION: action="REGISTER" and empty patientID for a new patient — confirm against your servlet.
                    ApiClient.ApiResponse<Void> resp = apiClient.savePatient("REGISTER", "", name, contact, address);
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
                    registerStatus.setForeground(new Color(0, 128, 0));
                    registerStatus.setText("Patient registered.");
                    nameField.setText("");
                    contactField.setText("");
                    addressField.setText("");
                } else {
                    registerStatus.setForeground(Color.RED);
                    registerStatus.setText(errorMessage != null ? errorMessage : "Registration failed (status " + statusCode + ").");
                }
            }
        }.execute();
    }

    private void searchPatients() {
        String query = searchField.getText().trim().toLowerCase();
        searchStatus.setForeground(Color.DARK_GRAY);
        searchStatus.setText("Searching...");
        searchResults.setText("");

        new SwingWorker<Void, Void>() {
            int statusCode;
            PatientDto[] patients;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    // No server-side search endpoint exists yet (getPatients() returns all) — filtering client-side for now.
                    ApiClient.ApiResponse<PatientDto[]> resp = apiClient.getPatients();
                    statusCode = resp.statusCode;
                    patients = resp.body;
                    errorMessage = resp.errorMessage;
                } catch (Exception ex) {
                    statusCode = -1;
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (statusCode == 200 && patients != null) {
                    StringBuilder sb = new StringBuilder();
                    for (PatientDto p : patients) {
                        if (query.isEmpty()
                                || p.getName().toLowerCase().contains(query)
                                || p.getPatientID().toLowerCase().contains(query)) {
                            sb.append(p.getPatientID()).append(" - ").append(p.getName())
                                    .append(" - ").append(p.getContactNo())
                                    .append(" - ").append(p.getAddress()).append("\n");
                        }
                    }
                    searchResults.setText(sb.length() > 0 ? sb.toString() : "No matches.");
                    searchStatus.setForeground(new Color(0, 128, 0));
                    searchStatus.setText("Done.");
                } else {
                    searchStatus.setForeground(Color.RED);
                    searchStatus.setText(errorMessage != null ? errorMessage : "Search failed.");
                }
            }
        }.execute();
    }
}