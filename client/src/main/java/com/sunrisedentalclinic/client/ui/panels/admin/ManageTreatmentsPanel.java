package com.sunrisedentalclinic.client.ui.panels.admin;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.dto.TreatmentDto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ManageTreatmentsPanel extends JPanel {

    private final String[] columns = {"Treatment ID", "Name", "Base Cost"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private final JTextField idField = new JTextField(15);
    private final JTextField nameField = new JTextField(20);
    private final JTextField costField = new JTextField(10);
    private final JButton saveButton = new JButton("Add Treatment");
    private final JButton newButton = new JButton("New Treatment");
    private final JButton deleteButton = new JButton("Delete Selected");
    private final JLabel statusLabel = new JLabel(" ");

    private boolean editMode = false;

    private final ApiClient apiClient;

    public ManageTreatmentsPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedIntoForm();
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Treatment ID:"));
        form.add(idField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Base Cost:"));
        form.add(costField);
        formPanel.add(form, BorderLayout.CENTER);

        statusLabel.setForeground(Color.RED);
        JPanel south = new JPanel(new BorderLayout());
        south.add(statusLabel, BorderLayout.CENTER);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteButton.setEnabled(false);
        buttonRow.add(deleteButton);
        buttonRow.add(newButton);
        buttonRow.add(saveButton);
        south.add(buttonRow, BorderLayout.SOUTH);
        formPanel.add(south, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> save());
        newButton.addActionListener(e -> resetToCreateMode());
        deleteButton.addActionListener(e -> delete());

        loadTreatments();
    }

    private void loadSelectedIntoForm() {
        int row = table.getSelectedRow();
        idField.setText((String) tableModel.getValueAt(row, 0));
        nameField.setText((String) tableModel.getValueAt(row, 1));
        costField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        idField.setEnabled(false);
        editMode = true;
        saveButton.setText("Update Treatment");
        deleteButton.setEnabled(true);
    }

    private void resetToCreateMode() {
        table.clearSelection();
        idField.setText("");
        nameField.setText("");
        costField.setText("");
        idField.setEnabled(true);
        editMode = false;
        saveButton.setText("Add Treatment");
        deleteButton.setEnabled(false);
        statusLabel.setText(" ");
    }

    private void loadTreatments() {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Loading treatments...");

        new SwingWorker<Void, Void>() {
            int statusCode;
            TreatmentDto[] treatments;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<TreatmentDto[]> resp = apiClient.getTreatments();
                    statusCode = resp.statusCode;
                    treatments = resp.body;
                    errorMessage = resp.errorMessage;
                } catch (Exception ex) {
                    statusCode = -1;
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (statusCode == 200 && treatments != null) {
                    tableModel.setRowCount(0);
                    for (TreatmentDto t : treatments) {
                        tableModel.addRow(new Object[]{t.getTreatmentID(), t.getTreatmentName(), t.getBaseCost()});
                    }
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText(treatments.length + " treatment(s) loaded.");
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Failed to load treatments.");
                }
            }
        }.execute();
    }

    private void save() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String cost = costField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || cost.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("All fields are required.");
            return;
        }

        double baseCost;
        try {
            baseCost = Double.parseDouble(cost);
        } catch (NumberFormatException ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Base Cost must be a number.");
            return;
        }
        if (baseCost <= 0) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Base Cost must be greater than zero.");
            return;
        }

        saveButton.setEnabled(false);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Saving...");

        Map<String, String> fields = new HashMap<>();
        fields.put("action", editMode ? "update" : "create");
        fields.put("treatmentID", id);
        fields.put("treatmentName", name);
        fields.put("baseCost", cost);

        new SwingWorker<Void, Void>() {
            int statusCode;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<Void> resp = apiClient.saveTreatment(fields);
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
                saveButton.setEnabled(true);
                if (statusCode >= 200 && statusCode < 300) {
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText(editMode ? "Treatment updated." : "Treatment added.");
                    resetToCreateMode();
                    loadTreatments();
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Save failed (status " + statusCode + ").");
                }
            }
        }.execute();
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String id = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete treatment " + id + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        deleteButton.setEnabled(false);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Deleting...");

        Map<String, String> fields = new HashMap<>();
        fields.put("action", "delete");
        fields.put("treatmentID", id);

        new SwingWorker<Void, Void>() {
            int statusCode;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<Void> resp = apiClient.saveTreatment(fields);
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
                    statusLabel.setText("Treatment deleted.");
                    resetToCreateMode();
                    loadTreatments();
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Delete failed (status " + statusCode + ").");
                    deleteButton.setEnabled(true);
                }
            }
        }.execute();
    }
}