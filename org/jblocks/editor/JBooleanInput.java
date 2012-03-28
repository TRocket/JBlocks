/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 *
 * Input class for boolean-blocks. <br />
 * 
 * @author ZeroLuck
 */
class JBooleanInput extends AbstrInput {

    private static Polygon getPlg(Dimension size) {
        Polygon plg = new Polygon();

        plg.addPoint(1, size.height / 2);
        plg.addPoint(size.height / 2, 1);
        plg.addPoint(size.width - size.height / 2, 1);
        plg.addPoint(size.width - 2, size.height / 2);
        plg.addPoint(size.width - size.height / 2 - 1, size.height - 2);
        plg.addPoint(size.height / 2 - 1, size.height - 2);

        return plg;
    }

    @Override
    public boolean contains(int x, int y) {
        return getPlg(getSize()).contains(x, y);
    }

    @Override
    public boolean accepts(AbstrBlock block) {
        if (!isBorderEnabled()) {
            return false;
        }
        return block instanceof JBooleanBlock;
    }

    @Override
    protected Insets getBorderInsets(int width, int height) {
        return new Insets(2, height / 2, 2, height / 2);
    }

    private static Color bright(Color c, float f) {
        return new Color(Math.min((int) (c.getRed() * f), 255),
                Math.min((int) (c.getGreen() * f), 255),
                Math.min((int) (c.getBlue() * f), 255));
    }

    @Override
    protected void paintBlockBorder(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Color col = getBackground();
        Stroke basic = g.getStroke();

        Polygon plg = getPlg(size);

        g.setColor(bright(col, 1.1F));
        g.fillPolygon(plg);

        g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
        g.setColor(col.darker());
        g.drawPolygon(plg);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setStroke(basic);
    }

    @Override
    public Dimension getDefaultSize() {
        return new Dimension(20, 12);
    }

    @Override
    public void reset() {
        setBorderEnabled(true);
        setInput(null);
    }
}
