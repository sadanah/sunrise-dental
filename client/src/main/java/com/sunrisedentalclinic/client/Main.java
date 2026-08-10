package com.sunrisedentalclinic.client;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

import com.sunrisedentalclinic.client.ui.LoginFrame;
import com.sunrisedentalclinic.client.ui.util.UIConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        FlatDraculaIJTheme.setup();

        // FlatLaf reads this key for every component's default font,
        // so bumping it here raises text size app-wide in one place.
        UIManager.put("defaultFont", UIConstants.BASE_FONT);

        ApiClient apiClient = new ApiClient();
        SwingUtilities.invokeLater(() -> new LoginFrame(apiClient).setVisible(true));
    }
}