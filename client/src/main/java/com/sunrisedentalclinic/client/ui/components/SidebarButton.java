package com.sunrisedentalclinic.client.ui.components;

import com.sunrisedentalclinic.client.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;

public class SidebarButton extends JButton {
    public SidebarButton(String text, Icon icon) {
        super(text, icon);
        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(10);
        setFont(UIConstants.BUTTON_FONT);
        setForeground(UIConstants.SIDEBAR_FG);
        setBackground(UIConstants.SIDEBAR_BG);
        setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 8));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { setBackground(UIConstants.SIDEBAR_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e) { setBackground(UIConstants.SIDEBAR_BG); }
        });
    }
}