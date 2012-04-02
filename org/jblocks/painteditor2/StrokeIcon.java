package org.jblocks.painteditor2;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.Icon;

/**
 * 
 * @author ZeroLuck
 */
class StrokeIcon implements Icon {

    private final BasicStroke stroke;
    private final Dimension max;

    public StrokeIcon(BasicStroke s, Dimension max) {
        this.stroke = s;
        this.max = max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintIcon(Component c, Graphics grp, int x, int y) {
        Graphics2D g = (Graphics2D) grp;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Rectangle clip = g.getClipBounds();
        g.setClip(clip.intersection(new Rectangle(x, y, max.width, max.height)));
        g.setStroke(stroke);
        int xp = (int) (x + max.width  /2 + stroke.getLineWidth() );
        int yp = (int) (y + max.height /2 + stroke.getLineWidth() );
        g.drawLine(xp, yp, xp, yp);
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setClip(clip);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIconWidth() {
        return max.width;
    }
    
    /**
     * 
     * @return - the icon's stroke.
     */
    public BasicStroke getStroke() {
        return stroke;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIconHeight() {
        return max.height;
    }
}
