package com.sunrisedentalclinic.client.ui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders an emoji glyph into a raster image and wraps it as an ImageIcon —
 * a true drop-in replacement for IconLoader.load("name.png") anywhere an
 * ImageIcon is expected (including code that casts Icon -> ImageIcon or
 * calls getImage() for scaling), not just anywhere a plain Icon works.
 */
public class EmojiIcon extends ImageIcon {

    public EmojiIcon(String glyph) {
        this(glyph, 18);
    }

    public EmojiIcon(String glyph, int size) {
        super(render(glyph, size));
    }

    private static Image render(String glyph, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Font font = new Font("Segoe UI Emoji", Font.PLAIN, (int) (size * 0.75));
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        int textWidth = fm.stringWidth(glyph);
        int textAscent = fm.getAscent();
        int drawX = (size - textWidth) / 2;
        int drawY = (size + textAscent) / 2 - fm.getDescent();

        g2.drawString(glyph, drawX, drawY);
        g2.dispose();
        return image;
    }
}