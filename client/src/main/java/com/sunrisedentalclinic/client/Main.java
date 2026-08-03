package com.sunrisedentalclinic.client;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient();
        SwingUtilities.invokeLater(() -> new LoginFrame(apiClient).setVisible(true));
    }
}