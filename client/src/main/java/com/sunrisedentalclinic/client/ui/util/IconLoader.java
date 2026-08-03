package com.sunrisedentalclinic.client.ui.util;

import javax.swing.*;
import java.net.URL;

public class IconLoader {
    public static ImageIcon load(String filename) {
        URL url = IconLoader.class.getResource("/icons/" + filename);
        return url != null ? new ImageIcon(url) : null;
    }
}