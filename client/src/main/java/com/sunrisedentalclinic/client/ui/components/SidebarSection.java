package com.sunrisedentalclinic.client.ui.components;

import com.sunrisedentalclinic.client.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;

public class SidebarSection extends JLabel {
    public SidebarSection(String text, Icon icon) {
        super(text, icon, SwingConstants.LEFT);
        setFont(UIConstants.SECTION_FONT);
        setForeground(UIConstants.SIDEBAR_FG);
        setBorder(BorderFactory.createEmptyBorder(14, 12, 4, 8));
        setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}