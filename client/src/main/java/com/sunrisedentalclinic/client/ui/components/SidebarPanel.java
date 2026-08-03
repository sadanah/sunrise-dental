package com.sunrisedentalclinic.client.ui.components;

import com.sunrisedentalclinic.client.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {

    public SidebarPanel(Consumer<String> onNavigate) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.SIDEBAR_BG);
        setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
    }

    public void addNavButton(String label, Icon icon, String navKey, Consumer<String> onNavigate) {
        SidebarButton btn = new SidebarButton(label, icon);
        btn.addActionListener(e -> onNavigate.accept(navKey));
        add(btn);
    }

    public void addSection(String label, Icon icon) {
        add(new SidebarSection(label, icon));
    }

    public void addGlue() {
        add(Box.createVerticalGlue());
    }
}