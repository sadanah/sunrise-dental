package com.sunrisedentalclinic.client.ui.panels.receptionist;

import com.sunrisedentalclinic.client.ApiClient;
import com.sunrisedentalclinic.client.dto.BillDto;

import javax.swing.*;
import java.awt.*;

public class GenerateBillPanel extends JPanel {

    private final JTextField appointmentNoField = new JTextField(15);
    private final JTextField discountField = new JTextField(5);
    private final JButton generateButton = new JButton("Generate Bill");
    private final JTextArea resultArea = new JTextArea(10, 40);
    private final JLabel statusLabel = new JLabel(" ");

    private final ApiClient apiClient;

    public GenerateBillPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formRow.add(new JLabel("Appointment No:"));
        formRow.add(appointmentNoField);
        formRow.add(new JLabel("Discount % (optional):"));
        formRow.add(discountField);
        formRow.add(generateButton);
        add(formRow, BorderLayout.NORTH);

        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        statusLabel.setForeground(Color.RED);
        add(statusLabel, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> generate());
    }

    private void generate() {
        String no = appointmentNoField.getText().trim();
        String discount = discountField.getText().trim();

        if (no.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Enter an appointment number.");
            return;
        }
        if (!discount.isEmpty()) {
            try {
                double d = Double.parseDouble(discount);
                if (d < 0 || d > 100) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Discount must be between 0 and 100.");
                    return;
                }
            } catch (NumberFormatException ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Discount must be a number.");
                return;
            }
        }

        generateButton.setEnabled(false);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Generating...");
        resultArea.setText("");

        new SwingWorker<Void, Void>() {
            int statusCode;
            BillDto bill;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<BillDto> resp = apiClient.generateBill(no, discount.isEmpty() ? null : discount);
                    statusCode = resp.statusCode;
                    bill = resp.body;
                    errorMessage = resp.errorMessage;
                } catch (Exception ex) {
                    statusCode = -1;
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                generateButton.setEnabled(true);
                if (statusCode >= 200 && statusCode < 300 && bill != null) {
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText("Bill generated.");
                    resultArea.setText(
                            "Bill ID: " + bill.getBillID() + "\n" +
                                    "Appointment No: " + bill.getAppointmentNo() + "\n" +
                                    "Consultation Fee: " + bill.getConsultationFee() + "\n" +
                                    "Treatment Cost: " + bill.getTreatmentCost() + "\n" +
                                    "Total Amount: " + bill.getTotalAmount() + "\n" +
                                    "Generated Date: " + bill.getGeneratedDate()
                    );
                } else if (statusCode == 404) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Appointment not found.");
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage != null ? errorMessage : "Unexpected error (status " + statusCode + ").");
                }
            }
        }.execute();
    }
}