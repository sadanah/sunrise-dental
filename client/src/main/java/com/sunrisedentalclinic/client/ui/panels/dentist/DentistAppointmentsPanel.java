package com.sunrisedentalclinic.client.ui.panels.dentist;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.AppSession;
import com.sunrisedentalclinic.client.dto.AppointmentDto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DentistAppointmentsPanel extends JPanel {

    private final JTextField searchField = new JTextField(15);
    private final JButton searchButton = new JButton("Search");
    private final JButton refreshButton = new JButton("Refresh");
    private final JLabel statusLabel = new JLabel(" ");

    private final String[] columns = {"Appointment No", "Patient ID", "Treatment ID", "Date", "Time", "Status"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private final ApiClient apiClient;
    private List<AppointmentDto> ownAppointments = new ArrayList<>();

    public DentistAppointmentsPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new JLabel("Search (Patient ID or Appointment No):"));
        searchRow.add(searchField);
        searchRow.add(searchButton);
        searchRow.add(refreshButton);
        add(searchRow, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        statusLabel.setForeground(Color.RED);

        searchButton.addActionListener(e -> applyFilter());
        searchField.addActionListener(e -> applyFilter());
        refreshButton.addActionListener(e -> { searchField.setText(""); loadOwnAppointments(); });

        loadOwnAppointments();
    }

    /** Called externally (e.g. when this panel becomes visible) to pick up newly created appointments. */
    public void refresh() {
        searchField.setText("");
        loadOwnAppointments();
    }

    private void loadOwnAppointments() {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Loading your appointments...");
        tableModel.setRowCount(0);
        String dentistID = AppSession.getStaffID();

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
                    ownAppointments = new ArrayList<>();
                    for (AppointmentDto a : appts) {
                        if (dentistID != null && dentistID.equals(a.getDentistID())) {
                            ownAppointments.add(a);
                        }
                    }
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText(ownAppointments.size() + " appointment(s).");
                    renderRows(ownAppointments);
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Failed to load appointments.");
                }
            }
        }.execute();
    }

    private void applyFilter() {
        String term = searchField.getText().trim();
        if (term.isEmpty()) {
            renderRows(ownAppointments);
            return;
        }
        List<AppointmentDto> filtered = new ArrayList<>();
        for (AppointmentDto a : ownAppointments) {
            if (term.equalsIgnoreCase(a.getPatientID()) || term.equalsIgnoreCase(a.getAppointmentNo())) {
                filtered.add(a);
            }
        }
        renderRows(filtered);
        statusLabel.setForeground(filtered.isEmpty() ? Color.RED : new Color(0, 128, 0));
        statusLabel.setText(filtered.isEmpty() ? "No match found." : filtered.size() + " match(es).");
    }

    private void renderRows(List<AppointmentDto> list) {
        tableModel.setRowCount(0);
        for (AppointmentDto a : list) {
            tableModel.addRow(new Object[]{
                    a.getAppointmentNo(), a.getPatientID(), a.getTreatmentID(),
                    a.getAppointmentDate(), a.getAppointmentTime(), a.getStatus()
            });
        }
    }
}