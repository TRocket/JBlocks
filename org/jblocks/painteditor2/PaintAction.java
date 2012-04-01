package org.jblocks.painteditor2;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author ZeroLuck
 */
public interface PaintAction {

    public void draw(BufferedImage img, Graphics2D g);
}
