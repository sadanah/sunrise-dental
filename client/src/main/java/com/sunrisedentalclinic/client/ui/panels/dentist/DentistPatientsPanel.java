package com.sunrisedentalclinic.client.ui.panels.dentist;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.AppSession;
import com.sunrisedentalclinic.client.dto.AppointmentDto;
import com.sunrisedentalclinic.client.dto.PatientDto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class DentistPatientsPanel extends JPanel {

    private final JButton refreshButton = new JButton("Refresh");
    private final JLabel statusLabel = new JLabel(" ");

    private final String[] columns = {"Patient ID", "Name", "Contact No", "Address"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private final ApiClient apiClient;

    public DentistPatientsPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Patients with appointments under your care:"));
        top.add(refreshButton);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        statusLabel.setForeground(Color.RED);

        refreshButton.addActionListener(e -> load());
        load();
    }

    public void refresh() {
        load();
    }

    private void load() {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Loading...");
        tableModel.setRowCount(0);
        String dentistID = AppSession.getStaffID();

        new SwingWorker<Void, Void>() {
            int apptStatus, patientStatus;
            AppointmentDto[] appts;
            PatientDto[] patients;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<AppointmentDto[]> apptResp = apiClient.getAllAppointments();
                    apptStatus = apptResp.statusCode;
                    appts = apptResp.body;

                    ApiClient.ApiResponse<PatientDto[]> patientResp = apiClient.getPatients();
                    patientStatus = patientResp.statusCode;
                    patients = patientResp.body;

                    if (apptStatus != 200 || patientStatus != 200) {
                        errorMessage = "Failed to load data from server.";
                    }
                } catch (Exception ex) {
                    apptStatus = -1;
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (errorMessage != null) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage);
                    return;
                }
                Set<String> ownPatientIDs = new HashSet<>();
                for (AppointmentDto a : appts) {
                    if (dentistID != null && dentistID.equals(a.getDentistID())) {
                        ownPatientIDs.add(a.getPatientID());
                    }
                }
                int count = 0;
                for (PatientDto p : patients) {
                    if (ownPatientIDs.contains(p.getPatientID())) {
                        tableModel.addRow(new Object[]{
                                p.getPatientID(), p.getName(), p.getContactNo(), p.getAddress()
                        });
                        count++;
                    }
                }
                statusLabel.setForeground(new Color(0, 128, 0));
                statusLabel.setText(count + " patient(s).");
            }
        }.execute();
    }
}