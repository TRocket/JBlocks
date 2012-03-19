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
 * A hat block. <br />
 * 
 * @author ZeroLuck
 */
public class HatBlock extends AbstrBlock implements Adapter {

    private static final float RND_X = 1 / 1.5F;
    private static final int RND_Y = 20;
    private static final int LEFT_RIGHT = 3;
    private static final int BOTTOM = 7;
    private static final int ADAPTER_W = 15;

    public HatBlock(JScriptPane pane) {
        super(pane);
    }

    @Override
    public void paintBorder(Graphics grp) {
        Color col = new Color(0xD6900A); //getBackground();
        Color dark = Color.BLACK;
        Color shadow = col.darker();
        Color darkShadow = shadow.darker();

        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Rectangle clip = g.getClipBounds();
        Stroke basic = g.getStroke();

        // Draw TOP
        g.setClip(0, 0, size.width, RND_Y);

        final int rndx = (int) (size.width * RND_X);
        final int rndy = RND_Y * 2;

        g.setPaint(new java.awt.GradientPaint(0, 0, shadow, 0, RND_Y, col));
        g.fillOval(0, 0, rndx, rndy);

        g.setColor(dark);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(0, 1, rndx, rndy - 1);
        g.drawLine(rndx + 1, RND_Y - 2, size.width - 5, RND_Y - 2);

        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setClip(clip);

        // draw LEFT and RIGHT
        g.setColor(darkShadow);
        g.drawLine(0, RND_Y, 0, size.height - BOTTOM);
        g.setColor(shadow);
        g.drawLine(1, RND_Y, 2, size.height - BOTTOM);

        g.setColor(darkShadow);
        g.drawLine(size.width - 1, RND_Y, size.width - 1, size.height - BOTTOM);
        g.setColor(shadow);
        g.drawLine(size.width - 3, RND_Y - 2, size.width - 3, size.height - BOTTOM);

        // draw BOTTOM
        g.setStroke(basic);
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

    @Override
    public Insets getBorderInsets(int width, int height) {
        return new Insets(RND_Y, LEFT_RIGHT, BOTTOM, LEFT_RIGHT);
    }

    @Override
    public Rectangle getAdapterBounds() {
        Dimension size = getSize();
        return new Rectangle(15, size.height - BOTTOM - 5, ADAPTER_W, BOTTOM + 4);
    }
}
