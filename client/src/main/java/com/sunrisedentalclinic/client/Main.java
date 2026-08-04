package com.sunrisedentalclinic.client;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

import com.sunrisedentalclinic.client.ui.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        FlatDraculaIJTheme.setup();

        ApiClient apiClient = new ApiClient();
        SwingUtilities.invokeLater(() -> new LoginFrame(apiClient).setVisible(true));
    }
}