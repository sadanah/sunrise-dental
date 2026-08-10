package com.sunrisedentalclinic.client.ui.components;

import com.sunrisedentalclinic.client.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Clickable shortcut card. Fires the dashboard's existing navigate(key)
 * callback on click, reusing the CardLayout keys already wired up in
 * each *DashboardFrame — no new routing needed.
 */
public class QuickNavCard extends JPanel {

    private Color bgColor = UIConstants.CARD_BG;

    public QuickNavCard(String glyph, String title, String subtitle, Color accent,
                        String navKey, Consumer<String> navigate) {
        setOpaque(false); // custom rounded background painted below
        setLayout(new BorderLayout(14, 0));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(220, 100));

        JLabel iconLabel = new JLabel(glyph, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(accent);
        iconLabel.setPreferredSize(new Dimension(44, 44));
        add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(UIConstants.CARD_TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(UIConstants.CARD_TEXT_SECONDARY);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(subLabel);
        add(textPanel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigate.accept(navKey); }
            @Override public void mouseEntered(MouseEvent e) { bgColor = UIConstants.CARD_HOVER_BG; repaint(); }
            @Override public void mouseExited(MouseEvent e) { bgColor = UIConstants.CARD_BG; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int r = UIConstants.CARD_RADIUS;

        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, r, r);

        g2.setColor(UIConstants.CARD_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, r, r);

        g2.dispose();
        super.paintComponent(g);
    }
}