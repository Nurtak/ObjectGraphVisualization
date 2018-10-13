package ch.hsr.ogv.util;

import javafx.scene.paint.Color;

/**
 * Util.java
 * <p>
 * Copyright (c) 2011-2015, JFXtras All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the organization nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class ColorUtil {

    public static double clamp(final double MIN, final double MAX, final double VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }

    public static Color brighter(final Color COLOR, final double FRACTION) {
        double red = clamp(0, 1, COLOR.getRed() * (1.0 + FRACTION));
        double green = clamp(0, 1, COLOR.getGreen() * (1.0 + FRACTION));
        double blue = clamp(0, 1, COLOR.getBlue() * (1.0 + FRACTION));
        return new Color(red, green, blue, COLOR.getOpacity());
    }

    public static Color darker(final Color COLOR, final double FRACTION) {
        double red = clamp(0, 1, COLOR.getRed() * (1.0 - FRACTION));
        double green = clamp(0, 1, COLOR.getGreen() * (1.0 - FRACTION));
        double blue = clamp(0, 1, COLOR.getBlue() * (1.0 - FRACTION));
        return new Color(red, green, blue, COLOR.getOpacity());
    }

    public static String colorToCssColor(final Color COLOR) {
        final StringBuilder CSS_COLOR = new StringBuilder(19);
        CSS_COLOR.append("rgba(");
        CSS_COLOR.append((int) (COLOR.getRed() * 255)).append(", ");
        CSS_COLOR.append((int) (COLOR.getGreen() * 255)).append(", ");
        CSS_COLOR.append((int) (COLOR.getBlue() * 255)).append(", ");
        CSS_COLOR.append(COLOR.getOpacity()).append(");");
        return CSS_COLOR.toString();
    }

    public static String colorToWebColor(final Color COLOR) {
        String red = Integer.toHexString((int) (COLOR.getRed() * 255));
        if (red.length() == 1) red = "0" + red;
        String green = Integer.toHexString((int) (COLOR.getGreen() * 255));
        if (green.length() == 1) green = "0" + green;
        String blue = Integer.toHexString((int) (COLOR.getBlue() * 255));
        if (blue.length() == 1) blue = "0" + blue;
        return "#" + red + green + blue;
    }

    /**
     * Converts hex color string to color supported formats 0xRRGGBB 0xRRGGBBAA #RRGGBB #RRGGBBAA RRGGBB RRGGBBAA
     *
     * @param COLOR
     * @return color given by hex string
     */
    public static Color webColorToColor(final String COLOR) {
        int red;
        int green;
        int blue;
        double alpha = 1.0;
        if (COLOR.startsWith("0x")) {
            red = Integer.valueOf(COLOR.substring(2, 4), 16);
            green = Integer.valueOf(COLOR.substring(4, 6), 16);
            blue = Integer.valueOf(COLOR.substring(6, 8), 16);
            if (COLOR.length() > 8) {
                alpha = 1.0 / 255.0 * (double) (Integer.valueOf(COLOR.substring(8, 10), 16));
            }
        }
        else if (COLOR.startsWith("#")) {
            red = Integer.valueOf(COLOR.substring(1, 3), 16);
            green = Integer.valueOf(COLOR.substring(3, 5), 16);
            blue = Integer.valueOf(COLOR.substring(5, 7), 16);
            if (COLOR.length() > 7) {
                alpha = 1.0 / 255.0 * (double) (Integer.valueOf(COLOR.substring(7, 9), 16));
            }
        }
        else {
            red = Integer.valueOf(COLOR.substring(0, 2), 16);
            green = Integer.valueOf(COLOR.substring(2, 4), 16);
            blue = Integer.valueOf(COLOR.substring(4, 6), 16);
            if (COLOR.length() > 6) {
                alpha = 1.0 / 255.0 * (double) (Integer.valueOf(COLOR.substring(6, 8), 16));
            }
        }
        return Color.rgb(red, green, blue, alpha);
    }

}
