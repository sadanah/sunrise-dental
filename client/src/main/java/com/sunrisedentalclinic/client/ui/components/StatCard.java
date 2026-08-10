package com.sunrisedentalclinic.client.ui.components;

import com.sunrisedentalclinic.client.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;

/** Icon + big number + label, styled to match the dark dashboard theme. */
public class StatCard extends JPanel {

    private final JLabel valueLabel;

    public StatCard(String glyph, String label, Color accent) {
        setOpaque(false); // custom rounded background painted below
        setLayout(new BorderLayout(12, 0));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setPreferredSize(new Dimension(220, 90));

        JLabel iconLabel = new JLabel(glyph, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(accent);
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setPreferredSize(new Dimension(52, 52));
        add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        valueLabel = new JLabel("…");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(UIConstants.CARD_TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel captionLabel = new JLabel(label);
        captionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        captionLabel.setForeground(UIConstants.CARD_TEXT_SECONDARY);
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(valueLabel);
        textPanel.add(captionLabel);
        add(textPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int r = UIConstants.CARD_RADIUS;

        g2.setColor(UIConstants.CARD_BG);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, r, r);

        g2.setColor(UIConstants.CARD_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, r, r);

        g2.dispose();
        super.paintComponent(g);
    }

    /** Call on the EDT once the async fetch resolves (or fails). */
    public void setValue(String text) {
        valueLabel.setText(text);
    }
}