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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 *
 * @author ZeroLuck
 */
public class BooleanBlock extends ReporterBlock {

    public BooleanBlock(JScriptPane p) {
        super(p);
    }

    @Override
    public void paintBorder(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Color col =  getBackground();
        Rectangle clip = g.getClipBounds();
        Stroke basic = g.getStroke();

        Polygon plg = new Polygon();

        plg.addPoint(1, size.height / 2);
        plg.addPoint(size.height / 2, 1);
        plg.addPoint(size.width - size.height / 2, 1);
        plg.addPoint(size.width - 2, size.height / 2);
        plg.addPoint(size.width - size.height / 2 - 1, size.height - 2);
        plg.addPoint(size.height / 2 - 1, size.height - 2);

        g.setColor(col);
        g.fillPolygon(plg);

        g.setStroke(new java.awt.BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
        g.setColor(col.darker());
        g.drawPolygon(plg);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setStroke(basic);
        g.setClip(clip);
    }

    @Override
    public Insets getBorderInsets(int width, int height) {
        return new Insets(2, height /2, 2, height /2);
    }
}
