package ch.hsr.ogv.util;

import javafx.scene.paint.Color;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class ColorUtil
{
    public static String toRGBCode( Color color ) {
        return String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
    }
}
