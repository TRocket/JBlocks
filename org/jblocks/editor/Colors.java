package org.jblocks.editor;

import java.awt.Color;

/**
 * Helper class for colors. <br />
 * 
 * @author ZeroLuck
 */
class Colors {

    /**
     * @param c the color
     * @param f the factor
     * @return the new color
     */
    public static Color bright(Color c, float f) {
        return new Color(Math.min((int) (c.getRed() * f), 255),
                Math.min((int) (c.getGreen() * f), 255),
                Math.min((int) (c.getBlue() * f), 255));
    }
}
