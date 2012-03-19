package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 *
 * @author ZeroLuck
 */
public class ReporterBlock extends AbstrBlock {

    public ReporterBlock(JScriptPane pane) {
        super(pane);
    }

    @Override
    public void paintBorder(Graphics grp) {
        if (this instanceof BooleanBlock) {
            throw new Error();
        }
        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Color col = getBackground();
        Rectangle clip = g.getClipBounds();
        Stroke basic = g.getStroke();

        g.setColor(col);
        g.fillRoundRect(0, 0, size.width, size.height, size.height / 2, size.height / 2);

        g.setStroke(new java.awt.BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
        g.setColor(col.darker());
        g.drawRoundRect(0, 0, size.width - 1, size.height - 1, size.height / 2, size.height / 2);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setStroke(basic);
        g.setClip(clip);
    }

    @Override
    public Insets getBorderInsets(int width, int height) {
        return new Insets(2, height / 2, 2, height / 2);
    }
}
