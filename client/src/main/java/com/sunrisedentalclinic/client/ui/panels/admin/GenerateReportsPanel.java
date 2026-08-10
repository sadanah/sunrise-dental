package com.sunrisedentalclinic.client.ui.panels.admin;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.dto.DentistDto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GenerateReportsPanel extends JPanel {

    private static final String REVENUE = "REVENUE";
    private static final String DAILY_APPOINTMENTS = "DAILY_APPOINTMENTS";
    private static final String DENTIST_SCHEDULE = "DENTIST_SCHEDULE";

    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{REVENUE, DAILY_APPOINTMENTS, DENTIST_SCHEDULE});
    private final JComboBox<ComboItem> dentistBox = new JComboBox<>();

    private final JSpinner startDateSpinner;
    private final JSpinner endDateSpinner;
    private final JLabel startDateLabel = new JLabel("Start Date:");
    private final JLabel endDateLabel = new JLabel("End Date:");
    private final JLabel dentistLabel = new JLabel("Dentist:");

    private final JButton generateButton = new JButton("Generate Report");
    private final JLabel statusLabel = new JLabel(" ");

    private final JLabel headerLabel = new JLabel(" ");
    private final JLabel revenueLabel = new JLabel(" ");

    private final CardLayout resultsCardLayout = new CardLayout();
    private final JPanel resultsCards = new JPanel(resultsCardLayout);

    private final String[] apptColumns = {"Appointment No", "Patient ID", "Dentist ID", "Treatment ID", "Date", "Time", "Status"};
    private final DefaultTableModel apptTableModel = new DefaultTableModel(apptColumns, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable apptTable = new JTable(apptTableModel);
    private final JScrollPane apptScrollPane = new JScrollPane(apptTable);

    private final ApiClient apiClient;

    public GenerateReportsPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        startDateSpinner = new JSpinner(new SpinnerDateModel(todayNoTime(), null, null, Calendar.DAY_OF_MONTH));
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        endDateSpinner = new JSpinner(new SpinnerDateModel(todayNoTime(), null, null, Calendar.DAY_OF_MONTH));
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Report Type:"), gbc);
        gbc.gridx = 1;
        form.add(typeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(dentistLabel, gbc);
        gbc.gridx = 1;
        form.add(dentistBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(startDateLabel, gbc);
        gbc.gridx = 1;
        form.add(startDateSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(endDateLabel, gbc);
        gbc.gridx = 1;
        form.add(endDateSpinner, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        form.add(generateButton, gbc);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.NORTH);
        statusLabel.setForeground(Color.RED);
        top.add(statusLabel, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JPanel resultsPanel = new JPanel(new BorderLayout(8, 8));
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
        resultsPanel.add(headerLabel, BorderLayout.NORTH);
        revenueLabel.setFont(revenueLabel.getFont().deriveFont(18f));

        resultsCards.add(revenueLabel, "REVENUE_VIEW");
        resultsCards.add(apptScrollPane, "TABLE_VIEW");
        resultsPanel.add(resultsCards, BorderLayout.CENTER);

        add(resultsPanel, BorderLayout.CENTER);

        typeBox.addActionListener(e -> updateFieldsForType());
        generateButton.addActionListener(e -> generate());

        updateFieldsForType();
        loadDentists();
    }

    private Date todayNoTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void updateFieldsForType() {
        String type = (String) typeBox.getSelectedItem();
        boolean isRevenue = REVENUE.equals(type);
        boolean isDentistSchedule = DENTIST_SCHEDULE.equals(type);

        dentistLabel.setVisible(isDentistSchedule);
        dentistBox.setVisible(isDentistSchedule);
        endDateLabel.setVisible(isRevenue);
        endDateSpinner.setVisible(isRevenue);
        startDateLabel.setText(isRevenue ? "Start Date:" : "Date:");
    }

    private void loadDentists() {
        new SwingWorker<Void, Void>() {
            DentistDto[] dentists;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<DentistDto[]> resp = apiClient.getDentists();
                    if (resp.statusCode == 200) dentists = resp.body;
                } catch (Exception ignored) { }
                return null;
            }

            @Override
            protected void done() {
                if (dentists != null) {
                    for (DentistDto d : dentists) {
                        dentistBox.addItem(new ComboItem(d.getStaffID(), d.getStaffID() + " - " + d.getName()));
                    }
                }
            }
        }.execute();
    }

    private void generate() {
        String type = (String) typeBox.getSelectedItem();
        String startDate = new SimpleDateFormat("yyyy-MM-dd").format((Date) startDateSpinner.getValue());
        String endDate = REVENUE.equals(type) ? new SimpleDateFormat("yyyy-MM-dd").format((Date) endDateSpinner.getValue()) : null;
        String dentistID = null;

        if (DENTIST_SCHEDULE.equals(type)) {
            ComboItem selected = (ComboItem) dentistBox.getSelectedItem();
            if (selected == null) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Please select a dentist.");
                return;
            }
            dentistID = selected.id;
        }

        generateButton.setEnabled(false);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Generating...");
//        revenueLabel.setVisible(false);
//        apptScrollPane.setVisible(false);
        headerLabel.setText(" ");

        String finalDentistID = dentistID;

        new SwingWorker<Void, Void>() {
            int statusCode;
            Map result;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<Map> resp = apiClient.generateReport(type, startDate, endDate, finalDentistID);
                    statusCode = resp.statusCode;
                    result = resp.body;
                    errorMessage = resp.errorMessage;
                } catch (Exception ex) {
                    statusCode = -1;
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                generateButton.setEnabled(true);
                if (statusCode == 200 && result != null) {
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText("Report generated.");
                    headerLabel.setText("Report ID: " + result.get("reportID")
                            + "   |   Generated By: " + result.get("generatedBy")
                            + "   |   Generated: " + result.get("generatedDate"));

                    if (REVENUE.equals(type)) {
                        revenueLabel.setText("Total Revenue: $" + result.get("totalRevenue"));
                        resultsCardLayout.show(resultsCards, "REVENUE_VIEW");
                    } else {
                        apptTableModel.setRowCount(0);
                        Object appointmentsObj = result.get("appointments");
                        if (appointmentsObj instanceof List) {
                            List<Map<String, Object>> appointments = (List<Map<String, Object>>) appointmentsObj;
                            for (Map<String, Object> a : appointments) {
                                apptTableModel.addRow(new Object[]{
                                        a.get("appointmentNo"), a.get("patientID"), a.get("dentistID"),
                                        a.get("treatmentID"), a.get("appointmentDate"), a.get("appointmentTime"), a.get("status")
                                });
                            }
                        }
                        resultsCardLayout.show(resultsCards, "TABLE_VIEW");
                    }
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Failed to generate report (status " + statusCode + ").");
                }
            }
        }.execute();
    }

    private static class ComboItem {
        final String id;
        final String label;
        ComboItem(String id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }
}