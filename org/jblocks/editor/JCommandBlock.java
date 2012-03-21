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
public class JCommandBlock extends AbstrBlock {

    private static final int TOP = 6;
    private static final int LEFT_RIGHT = 2;
    private static final int BOTTOM = TOP;
    private static final int ADAPTER_W = 15;

    public JCommandBlock(JScriptPane pane) {
        super(pane);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void paintBlockBorder(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        Color col = getBackground();
        Rectangle clip = g.getClipBounds();
        Dimension size = getSize();
        Stroke basic = g.getStroke();

        Color shadow = col.darker();
        Color darkShadow = shadow.darker();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new java.awt.BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));

        // TOP
        g.setColor(col);
        g.fillRect(0, 0, 15, TOP);
        g.fillRect(15 + ADAPTER_W, 0, size.width, TOP);
        g.setColor(shadow);
        g.drawRoundRect(15, -10, 15, 10 + TOP, 5, 5);
        g.drawLine(0, 0, 15, 0);
        g.drawLine(15 + ADAPTER_W, 0, size.width - 2, 0);

        // draw LEFT and RIGHT
        g.setColor(shadow);
        g.drawLine(0, 0, 0, size.height - BOTTOM);


        g.setStroke(basic);
        g.setColor(darkShadow);
        g.drawLine(size.width - 1, 0, size.width - 1, size.height - BOTTOM);
        g.setColor(shadow);
        g.drawLine(size.width - 2, 2, size.width - 2, size.height - BOTTOM);


        // draw BOTTOM
        g.setClip(0, size.height - BOTTOM, size.width, BOTTOM);

        g.setColor(shadow);
        g.drawLine(0, size.height - BOTTOM, size.width, size.height - BOTTOM);
        g.setColor(darkShadow);
        g.drawLine(1, size.height - BOTTOM + 1, size.width - 1, size.height - BOTTOM + 1);

        g.setColor(col);
        g.fillRoundRect(15, size.height - BOTTOM - 5, ADAPTER_W, BOTTOM + 5, 5, 5);
        g.setColor(darkShadow);
        g.drawRoundRect(15, size.height - BOTTOM - 5, ADAPTER_W, BOTTOM + 4, 5, 5);

        g.setClip(clip);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Insets getBorderInsets(int width, int height) {
        return new Insets(TOP, LEFT_RIGHT, BOTTOM, LEFT_RIGHT);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean contains(int x, int y) {
        Dimension size = getSize();
        Polygon pol = new Polygon();
        pol.addPoint(0, 0);
        pol.addPoint(15, 0);
        pol.addPoint(15, TOP);
        pol.addPoint(15 + ADAPTER_W, TOP);
        pol.addPoint(15 + ADAPTER_W, 0);
        pol.addPoint(size.width - 1, 0);
        pol.addPoint(size.width - 1, size.height - BOTTOM);
        pol.addPoint(15 + ADAPTER_W , size.height - BOTTOM);
        pol.addPoint(15 + ADAPTER_W, size.height - 1);
        pol.addPoint(15, size.height - 1);
        pol.addPoint(15, size.height - BOTTOM);
        pol.addPoint(0, size.height - BOTTOM);
        return pol.contains(x, y);
    }
}
