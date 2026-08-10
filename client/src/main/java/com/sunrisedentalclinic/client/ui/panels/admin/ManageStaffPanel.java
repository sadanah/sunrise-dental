package com.sunrisedentalclinic.client.ui.panels.admin;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.dto.StaffDto;

import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ManageStaffPanel extends JPanel {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z_]{3,}$");

    private static final Pattern PASSWORD_SPECIAL_PATTERN =
            Pattern.compile(".*[^A-Za-z0-9].*");

    private StaffDto[] allStaff = new StaffDto[0];
    private final String[] columns = {"Staff ID", "Name", "Role", "Username"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private final JTextField idField = new JTextField(15);
    private final JTextField nameField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField addressField = new JTextField(20);
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JComboBox<String> roleBox = new JComboBox<>(new String[]{"RECEPTIONIST", "DENTIST", "ADMIN"});
    private final JTextField specializationField = new JTextField(20);
    private final JTextField consultationFeeField = new JTextField(20);
    private final JLabel passwordHint = new JLabel("Required for new staff. Leave blank on edit to keep current password.");

    private final JButton saveButton = new JButton("Add Staff");
    private final JButton newButton = new JButton("New Staff");
    private final JButton deleteButton = new JButton("Delete Selected");
    private final JLabel statusLabel = new JLabel(" ");

    private boolean editMode = false;

    private final ApiClient apiClient;

    public ManageStaffPanel(ApiClient apiClient) {
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
        form.add(new JLabel("Staff ID:"));
        form.add(idField);
        form.add(new JLabel("Role:"));
        form.add(roleBox);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Contact No:"));
        form.add(contactField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Address:"));
        form.add(addressField);
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Specialization:"));
        form.add(specializationField);
        form.add(new JLabel("Consultation Fee:"));
        form.add(consultationFeeField);

        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.add(form, BorderLayout.CENTER);
        passwordHint.setFont(passwordHint.getFont().deriveFont(Font.ITALIC, 11f));
        passwordHint.setForeground(Color.GRAY);
        formWrapper.add(passwordHint, BorderLayout.SOUTH);
        formPanel.add(formWrapper, BorderLayout.CENTER);

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

        roleBox.addActionListener(e -> updateDentistFieldsEnabled());
        saveButton.addActionListener(e -> save());
        newButton.addActionListener(e -> resetToCreateMode());
        deleteButton.addActionListener(e -> delete());

        updateDentistFieldsEnabled();
        loadStaff();
    }

    private void updateDentistFieldsEnabled() {
        boolean isDentist = "DENTIST".equals(roleBox.getSelectedItem());
        specializationField.setEnabled(isDentist);
        consultationFeeField.setEnabled(isDentist);
        if (!isDentist) {
            specializationField.setText("");
            consultationFeeField.setText("");
        }
    }

    private void loadSelectedIntoForm() {
        int row = table.getSelectedRow();
        String selectedID = (String) tableModel.getValueAt(row, 0);

        StaffDto match = null;
        for (StaffDto s : allStaff) {
            if (s.getStaffID().equals(selectedID)) { match = s; break; }
        }
        if (match == null) return; // shouldn't happen, but guard against stale table state

        idField.setText(match.getStaffID());
        nameField.setText(match.getName());
        contactField.setText(match.getContactNo());
        emailField.setText(match.getEmail() != null ? match.getEmail() : "");
        addressField.setText(match.getAddress());
        usernameField.setText(match.getUsername());
        passwordField.setText("");
        roleBox.setSelectedItem(match.getRole());
        specializationField.setText(match.getSpecialization() != null ? match.getSpecialization() : "");
        consultationFeeField.setText(match.getConsultationFee() != null ? match.getConsultationFee() : "");

        idField.setEnabled(false);
        roleBox.setEnabled(false); // role cannot be changed via update — StaffDAO.update() doesn't persist it
        editMode = true;
        saveButton.setText("Update Staff");
        deleteButton.setEnabled(true);
        updateDentistFieldsEnabled();
    }

    private void resetToCreateMode() {
        table.clearSelection();
        idField.setText("");
        nameField.setText("");
        contactField.setText("");
        emailField.setText("");
        addressField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        roleBox.setSelectedIndex(0);
        idField.setEnabled(true);
        roleBox.setEnabled(true);
        editMode = false;
        saveButton.setText("Add Staff");
        deleteButton.setEnabled(false);
        statusLabel.setText(" ");
        updateDentistFieldsEnabled();
    }

    private void loadStaff() {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Loading staff...");

        new SwingWorker<Void, Void>() {
            int statusCode;
            StaffDto[] staff;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<StaffDto[]> resp = apiClient.getStaff();
                    statusCode = resp.statusCode;
                    staff = resp.body;
                    errorMessage = resp.errorMessage;
                } catch (Exception ex) {
                    statusCode = -1;
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (statusCode == 200 && staff != null) {
                    allStaff = staff;
                    tableModel.setRowCount(0);
                    for (StaffDto s : staff) {
                        tableModel.addRow(new Object[]{s.getStaffID(), s.getName(), s.getRole(), s.getUsername()});
                    }
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText(staff.length + " staff member(s) loaded.");
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Failed to load staff.");
                }
            }
        }.execute();
    }

    private void save() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleBox.getSelectedItem();
        String specialization = specializationField.getText().trim();
        String fee = consultationFeeField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || contact.isEmpty() || address.isEmpty() || username.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("All fields except password (on edit) are required.");
            return;
        }
//        if (!editMode && password.isEmpty()) {
//            statusLabel.setForeground(Color.RED);
//            statusLabel.setText("Password is required for new staff.");
//            return;
//        }
        // fake email validation
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please enter a valid email address.");
            return;
        }

// username
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Username must be at least 3 characters and contain only letters and underscores.");
            return;
        }

// password (new staff)
        if (!editMode) {
            if (password.length() < 8) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Password must be at least 8 characters.");
                return;
            }

            if (!PASSWORD_SPECIAL_PATTERN.matcher(password).matches()) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Password must contain at least one special character.");
                return;
            }
        }

// password (editing existing staff)
        if (editMode && !password.isBlank()) {
            if (password.length() < 8) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Password must be at least 8 characters.");
                return;
            }

            if (!PASSWORD_SPECIAL_PATTERN.matcher(password).matches()) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Password must contain at least one special character.");
                return;
            }
        }

        if ("DENTIST".equals(role) && (specialization.isEmpty() || fee.isEmpty())) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Specialization and Consultation Fee are required for Dentists.");
            return;
        }
        if ("DENTIST".equals(role)) {
            try {
                Double.parseDouble(fee);
            } catch (NumberFormatException ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Consultation Fee must be a number.");
                return;
            }
        }

        saveButton.setEnabled(false);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Saving...");

        Map<String, String> fields = new HashMap<>();
        fields.put("action", editMode ? "update" : "create");
        fields.put("staffID", id);
        fields.put("name", name);
        fields.put("contactNo", contact);
        fields.put("email", email);
        fields.put("address", address);
        fields.put("username", username);
        fields.put("password", password);
        fields.put("role", role);
        fields.put("specialization", "DENTIST".equals(role) ? specialization : "");
        fields.put("consultationFee", "DENTIST".equals(role) ? fee : "0");

        new SwingWorker<Void, Void>() {
            int statusCode;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<Void> resp = apiClient.saveStaff(fields);
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
                    statusLabel.setText(editMode ? "Staff updated." : "Staff added.");
                    resetToCreateMode();
                    loadStaff();
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
                "Delete staff member " + id + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        deleteButton.setEnabled(false);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Deleting...");

        Map<String, String> fields = new HashMap<>();
        fields.put("action", "delete");
        fields.put("staffID", id);

        new SwingWorker<Void, Void>() {
            int statusCode;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<Void> resp = apiClient.saveStaff(fields);
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
                    statusLabel.setText("Staff deleted.");
                    resetToCreateMode();
                    loadStaff();
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Delete failed (status " + statusCode + ").");
                    deleteButton.setEnabled(true);
                }
            }
        }.execute();
    }
}