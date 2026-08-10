package com.sunrisedentalclinic.client.ui.util;

import java.awt.*;

public class UIConstants {
    public static final int SIDEBAR_WIDTH = 220;
    public static final Color SIDEBAR_BG = new Color(38, 45, 58);
    public static final Color SIDEBAR_FG = Color.WHITE;
    public static final Color SIDEBAR_HOVER = new Color(55, 64, 80);

    // Brand
    public static final Color BRAND_BLUE = new Color(0x274671);
    public static final Color BRAND_TEXT = Color.WHITE;

    // Base font bumped up from the old 12/13pt defaults — this is applied
    // globally via UIManager in Main.java, so every screen picks it up.
    public static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font SECTION_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.PLAIN, 14);

    // Card styling (dashboard stat/quick-nav cards)
    public static final Color CARD_BG = new Color(0x323A4E);
    public static final Color CARD_BORDER = new Color(0x454E66);
    public static final Color CARD_HOVER_BG = new Color(0x3A4258);
    public static final Color CARD_TEXT_PRIMARY = Color.WHITE;
    public static final Color CARD_TEXT_SECONDARY = new Color(0xB9C0D0);
    public static final int CARD_RADIUS = 10;
}