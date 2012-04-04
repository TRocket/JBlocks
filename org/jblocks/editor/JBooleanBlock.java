package org.jblocks.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;

/**
 *
 * @author ZeroLuck
 */
class JBooleanBlock extends JReporterBlock {

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
    protected void paintBlockBorder(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Color col = getBackground();
      //  Stroke basic = g.getStroke();

        Polygon plg = getPlg(size);
        
        g.setColor(col);
        g.fillPolygon(plg);

      //  g.setStroke(new java.awt.BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
        g.setColor(col.darker());
        g.drawPolygon(plg);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

      //  g.setStroke(basic);
    }

    @Override
    protected Insets getBorderInsets(int width, int height) {
        return new Insets(2, height / 2, 2, height / 2);
    }

    @Override
    public boolean contains(int x, int y) {
        return getPlg(getSize()).contains(x, y);
    }
}
