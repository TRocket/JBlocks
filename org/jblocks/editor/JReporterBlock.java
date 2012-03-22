package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author ZeroLuck
 */
public class JReporterBlock extends AbstrBlock {

    public JReporterBlock(JScriptPane pane) {
        super(pane);
    }

    @Override
    public void paintBlockBorder(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Color col = getBackground();
        Stroke basic = g.getStroke();

        g.setColor(col);
        g.fillRoundRect(0, 0, size.width, size.height, size.height / 2, size.height / 2);

        g.setStroke(new java.awt.BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
        g.setColor(col.darker());
        g.drawRoundRect(0, 0, size.width - 1, size.height - 1, size.height / 2, size.height / 2);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setStroke(basic);
    }

    @Override
    public Insets getBorderInsets(int width, int height) {
        return new Insets(2, height / 4, 2, height / 4);
    }

    @Override
    public boolean contains(int x, int y) {
        Dimension size = getSize();

        RoundRectangle2D.Float rect = new RoundRectangle2D.Float(0, 0, size.width, size.height, size.height / 4, size.height / 4);

        return rect.contains(x, y);
    }

    @Override
    public void dragEvent(MouseEvent evt) {
    }

    @Override
    public void pressedEvent(MouseEvent evt) {
    }

    @Override
    public void releasedEvent(MouseEvent evt) {
    }
}
