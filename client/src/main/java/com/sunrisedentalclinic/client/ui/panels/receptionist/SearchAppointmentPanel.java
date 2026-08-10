package com.sunrisedentalclinic.client.ui.panels.receptionist;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.dto.AppointmentDto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchAppointmentPanel extends JPanel {

    private final JTextField appointmentNoField = new JTextField(15);
    private final JButton searchButton = new JButton("Search");
    private final JButton viewAllButton = new JButton("View All Appointments");
    private final JButton cancelButton = new JButton("Cancel Selected");
    private final JLabel statusLabel = new JLabel(" ");

    private final String[] columns = {"Appointment No", "Patient ID", "Dentist ID", "Treatment ID", "Date", "Time", "Status"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private final ApiClient apiClient;

    public SearchAppointmentPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new JLabel("Appointment No:"));
        searchRow.add(appointmentNoField);
        searchRow.add(searchButton);
        searchRow.add(viewAllButton);
        add(searchRow, BorderLayout.NORTH);

        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cancelButton.setEnabled(table.getSelectedRow() != -1 && isCancellable(table.getSelectedRow()));
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        statusLabel.setForeground(Color.RED);
        south.add(statusLabel, BorderLayout.CENTER);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelButton.setEnabled(false);
        buttonRow.add(cancelButton);
        south.add(buttonRow, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> search());
        viewAllButton.addActionListener(e -> viewAll());
        cancelButton.addActionListener(e -> cancel());
        appointmentNoField.addActionListener(e -> search());
    }

    private boolean isCancellable(int row) {
        String status = (String) tableModel.getValueAt(row, 6);
        return "SCHEDULED".equals(status);
    }

    private void clearTable() {
        tableModel.setRowCount(0);
        cancelButton.setEnabled(false);
    }

    private void addRow(AppointmentDto a) {
        tableModel.addRow(new Object[]{
                a.getAppointmentNo(), a.getPatientID(), a.getDentistID(),
                a.getTreatmentID(), a.getAppointmentDate(), a.getAppointmentTime(), a.getStatus()
        });
    }

    private void search() {
        String no = appointmentNoField.getText().trim();
        if (no.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Enter an appointment number, or click 'View All Appointments'.");
            return;
        }
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Searching...");
        clearTable();

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
                    addRow(appt);
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Appointment not found.");
                }
            }
        }.execute();
    }

    private void viewAll() {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Loading all appointments...");
        clearTable();

        new SwingWorker<Void, Void>() {
            int statusCode;
            AppointmentDto[] appts;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<AppointmentDto[]> resp = apiClient.getAllAppointments();
                    statusCode = resp.statusCode;
                    appts = resp.body;
                    errorMessage = resp.errorMessage;
                } catch (Exception ex) {
                    statusCode = -1;
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (statusCode == 200 && appts != null) {
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText(appts.length + " appointment(s) found.");
                    for (AppointmentDto a : appts) addRow(a);
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Failed to load appointments.");
                }
            }
        }.execute();
    }

    private void cancel() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String appointmentNo = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel appointment " + appointmentNo + "?",
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
                    ApiClient.ApiResponse<Void> resp = apiClient.cancelAppointment(appointmentNo);
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
                    tableModel.setValueAt("CANCELLED", row, 6);
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Cancel failed (status " + statusCode + ").");
                    cancelButton.setEnabled(true);
                }
            }
        }.execute();
    }
}