package com.sunrisedentalclinic.client.ui.panels.receptionist;

import com.sunrisedentalclinic.client.AppSession;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    public HomePanel() {
        setLayout(new BorderLayout());
        JLabel welcome = new JLabel("Welcome, " + AppSession.getStaffID() + " (Dentist)");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 16f));
        welcome.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(welcome, BorderLayout.NORTH);
    }
}