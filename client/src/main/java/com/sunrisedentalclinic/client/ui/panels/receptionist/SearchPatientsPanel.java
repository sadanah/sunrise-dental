package com.sunrisedentalclinic.client.ui.panels.receptionist;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.dto.PatientDto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchPatientsPanel extends JPanel {

    private final JTextField searchField = new JTextField(15);
    private final JButton searchButton = new JButton("Filter");
    private final JButton viewAllButton = new JButton("View All Patients");
    private final JButton deleteButton = new JButton("Delete Selected");
    private final JLabel statusLabel = new JLabel(" ");

    private final String[] columns = {"Patient ID", "Name", "Contact No", "Address"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private PatientDto[] allPatients = new PatientDto[0];

    private final ApiClient apiClient;

    public SearchPatientsPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new JLabel("Name or Patient ID contains:"));
        searchRow.add(searchField);
        searchRow.add(searchButton);
        searchRow.add(viewAllButton);
        add(searchRow, BorderLayout.NORTH);

        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(table.getSelectedRow() != -1);
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        statusLabel.setForeground(Color.RED);
        south.add(statusLabel, BorderLayout.CENTER);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteButton.setEnabled(false);
        buttonRow.add(deleteButton);
        south.add(buttonRow, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> applyFilter());
        viewAllButton.addActionListener(e -> loadAll());
        deleteButton.addActionListener(e -> deleteSelected());
        searchField.addActionListener(e -> applyFilter());
    }

    private void fillTable(PatientDto[] patients) {
        tableModel.setRowCount(0);
        for (PatientDto p : patients) {
            tableModel.addRow(new Object[]{p.getPatientID(), p.getName(), p.getContactNo(), p.getAddress()});
        }
        deleteButton.setEnabled(false);
    }

    private void loadAll() {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Loading all patients...");

        new SwingWorker<Void, Void>() {
            int statusCode;
            PatientDto[] patients;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
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
                    allPatients = patients;
                    fillTable(patients);
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText(patients.length + " patient(s) found.");
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Failed to load patients.");
                }
            }
        }.execute();
    }

    // Filters the already-loaded list client-side — no server-side search endpoint exists yet
    // (see ASSUMPTIONS.md: acceptable at current scale, would need a real search endpoint at
    // production patient volumes).
    private void applyFilter() {
        String query = searchField.getText().trim().toLowerCase();
        if (allPatients.length == 0) {
            loadAll();
            return;
        }
        java.util.List<PatientDto> matches = new java.util.ArrayList<>();
        for (PatientDto p : allPatients) {
            if (query.isEmpty()
                    || p.getName().toLowerCase().contains(query)
                    || p.getPatientID().toLowerCase().contains(query)) {
                matches.add(p);
            }
        }
        fillTable(matches.toArray(new PatientDto[0]));
        statusLabel.setForeground(new Color(0, 128, 0));
        statusLabel.setText(matches.size() + " match(es).");
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String patientID = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete patient " + patientID + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        deleteButton.setEnabled(false);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Deleting...");

        new SwingWorker<Void, Void>() {
            int statusCode;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<Void> resp = apiClient.savePatient("delete", patientID, "", "", "");
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
                    statusLabel.setText("Patient deleted.");
                    tableModel.removeRow(row);
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Delete failed (status " + statusCode + ").");
                    deleteButton.setEnabled(true);
                }
            }
        }.execute();
    }
}