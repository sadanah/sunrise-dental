package com.sunrisedentalclinic.client;

import com.sunrisedentalclinic.client.dto.*;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterAppointmentDialog extends JDialog {

    private final JComboBox<ComboItem> patientBox = new JComboBox<>();
    private final JComboBox<ComboItem> dentistBox = new JComboBox<>();
    private final JComboBox<ComboItem> treatmentBox = new JComboBox<>();
    private final JSpinner dateSpinner;
    private final JSpinner timeSpinner;
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton submitButton = new JButton("Register");

    private final ApiClient apiClient;

    public RegisterAppointmentDialog(Frame owner, ApiClient apiClient) {
        super(owner, "Register Appointment", true);
        this.apiClient = apiClient;
        setSize(420, 340);
        setLocationRelativeTo(owner);

        // Date spinner: defaults to today, "roll" via up/down arrows
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        // Time spinner: defaults to now, rolls by the hour field but full HH:mm editable
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, (cal.get(Calendar.MINUTE) / 15) * 15); // round to nearest 15 min
        timeSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.MINUTE));
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        form.add(new JLabel("Patient:"));
        form.add(patientBox);
        form.add(new JLabel("Dentist:"));
        form.add(dentistBox);
        form.add(new JLabel("Treatment:"));
        form.add(treatmentBox);
        form.add(new JLabel("Date:"));
        form.add(dateSpinner);
        form.add(new JLabel("Time:"));
        form.add(timeSpinner);

        statusLabel.setForeground(Color.RED);

        JPanel south = new JPanel(new BorderLayout());
        south.add(statusLabel, BorderLayout.CENTER);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonRow.add(cancelButton);
        buttonRow.add(submitButton);
        south.add(buttonRow, BorderLayout.SOUTH);

        setLayout(new BorderLayout(10, 10));
        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> submit());
        setDropdownsLoading(true);
        loadDropdownData();
    }

    private void setDropdownsLoading(boolean loading) {
        patientBox.setEnabled(!loading);
        dentistBox.setEnabled(!loading);
        treatmentBox.setEnabled(!loading);
        submitButton.setEnabled(!loading);
    }

    private void loadDropdownData() {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Loading patients/dentists/treatments...");

        new SwingWorker<Void, Void>() {
            PatientDto[] patients;
            DentistDto[] dentists;
            TreatmentDto[] treatments;
            String error;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<PatientDto[]> p = apiClient.getPatients();
                    ApiClient.ApiResponse<DentistDto[]> d = apiClient.getDentists();
                    ApiClient.ApiResponse<TreatmentDto[]> t = apiClient.getTreatments();

                    if (p.statusCode != 200 || d.statusCode != 200 || t.statusCode != 200) {
                        error = "Failed to load dropdown data (check server connection).";
                        return null;
                    }
                    patients = p.body;
                    dentists = d.body;
                    treatments = t.body;
                } catch (Exception ex) {
                    error = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (error != null) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(error);
                    return;
                }
                for (PatientDto p : patients) {
                    patientBox.addItem(new ComboItem(p.getPatientID(),
                            p.getPatientID() + " - " + p.getName()));
                }
                for (DentistDto d : dentists) {
                    dentistBox.addItem(new ComboItem(d.getStaffID(),
                            d.getStaffID() + " - " + d.getName() + " (" + d.getSpecialization() + ")"));
                }
                for (TreatmentDto t : treatments) {
                    treatmentBox.addItem(new ComboItem(t.getTreatmentID(),
                            t.getTreatmentName() + " - $" + t.getBaseCost()));
                }
                statusLabel.setText(" ");
                setDropdownsLoading(false);
            }
        }.execute();
    }

    private void submit() {
        ComboItem patient = (ComboItem) patientBox.getSelectedItem();
        ComboItem dentist = (ComboItem) dentistBox.getSelectedItem();
        ComboItem treatment = (ComboItem) treatmentBox.getSelectedItem();

        if (patient == null || dentist == null || treatment == null) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("All fields are required.");
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format((Date) dateSpinner.getValue());
        String time = new SimpleDateFormat("HH:mm").format((Date) timeSpinner.getValue());

        submitButton.setEnabled(false);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Submitting...");

        new SwingWorker<Void, Void>() {
            int statusCode;
            String appointmentNo;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<AppointmentDto> resp = apiClient.registerAppointment(
                            patient.id, dentist.id, treatment.id, date, time);
                    statusCode = resp.statusCode;
                    if (resp.body != null) {
                        appointmentNo = resp.body.getAppointmentNo();
                    }
                    errorMessage = resp.errorMessage;
                } catch (Exception ex) {
                    statusCode = -1;
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                submitButton.setEnabled(true);
                if (statusCode == 201) {
                    JOptionPane.showMessageDialog(RegisterAppointmentDialog.this,
                            "Appointment registered.\nAppointment No: " + appointmentNo,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else if (statusCode == 409) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Slot unavailable: " + errorMessage);
                } else if (statusCode == 400) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Invalid request: " + errorMessage);
                } else if (statusCode == 403) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Forbidden: receptionist role required.");
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage
                            : "Unexpected error (status " + statusCode + ").");
                }
            }
        }.execute();
    }

    /** Wraps an ID with a display label for combo boxes. */
    private static class ComboItem {
        final String id;
        final String label;

        ComboItem(String id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}