package com.sunrisedentalclinic.client.ui.panels.receptionist;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.dto.AppointmentDto;

import javax.swing.*;
import java.awt.*;

public class SearchAppointmentPanel extends JPanel {

    private final JTextField appointmentNoField = new JTextField(15);
    private final JButton searchButton = new JButton("Search");
    private final JButton cancelButton = new JButton("Cancel Appointment");
    private final JTextArea resultArea = new JTextArea(10, 40);
    private final JLabel statusLabel = new JLabel(" ");

    private final ApiClient apiClient;
    private String currentAppointmentNo;

    public SearchAppointmentPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new JLabel("Appointment No:"));
        searchRow.add(appointmentNoField);
        searchRow.add(searchButton);
        add(searchRow, BorderLayout.NORTH);

        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        statusLabel.setForeground(Color.RED);
        south.add(statusLabel, BorderLayout.CENTER);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelButton.setEnabled(false);
        buttonRow.add(cancelButton);
        south.add(buttonRow, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> search());
        cancelButton.addActionListener(e -> cancel());
    }

    private void search() {
        String no = appointmentNoField.getText().trim();
        if (no.isEmpty()) {
            statusLabel.setText("Enter an appointment number.");
            return;
        }
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Searching...");
        cancelButton.setEnabled(false);
        resultArea.setText("");

        new SwingWorker<Void, Void>() {
            int statusCode;
            AppointmentDto appt;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<AppointmentDto> resp = apiClient.searchAppointment(no);
                    statusCode = resp.statusCode;
                    appt = resp.body;
                    errorMessage = resp.errorMessage;
                } catch (Exception ex) {
                    statusCode = -1;
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (statusCode == 200 && appt != null) {
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText("Found.");
                    currentAppointmentNo = appt.getAppointmentNo();
                    resultArea.setText(
                            "Appointment No: " + appt.getAppointmentNo() + "\n" +
                                    "Patient ID: " + appt.getPatientID() + "\n" +
                                    "Dentist ID: " + appt.getDentistID() + "\n" +
                                    "Treatment ID: " + appt.getTreatmentID() + "\n" +
                                    "Date: " + appt.getAppointmentDate() + "\n" +
                                    "Time: " + appt.getAppointmentTime() + "\n" +
                                    "Status: " + appt.getStatus()
                    );
                    cancelButton.setEnabled(true);
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Appointment not found.");
                    cancelButton.setEnabled(false);
                }
            }
        }.execute();
    }

    private void cancel() {
        if (currentAppointmentNo == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel appointment " + currentAppointmentNo + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        cancelButton.setEnabled(false);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Cancelling...");

        new SwingWorker<Void, Void>() {
            int statusCode;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<Void> resp = apiClient.cancelAppointment(currentAppointmentNo);
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
                if (statusCode >= 200 && statusCode < 300) {
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText("Appointment cancelled.");
                    resultArea.setText("");
                    currentAppointmentNo = null;
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Cancel failed (status " + statusCode + ").");
                    cancelButton.setEnabled(true);
                }
            }
        }.execute();
    }
}