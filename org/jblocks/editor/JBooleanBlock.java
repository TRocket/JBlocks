package org.jblocks.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

/**
 *
 * @author ZeroLuck
 */
class JBooleanBlock extends JReporterBlock {

    /**
     * See {@link AbstrBlock#AbstrBlock(org.jblocks.editor.BlockModel) }
     */
    public JBooleanBlock(BlockModel model) {
        super(model);
    }

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
        Shape clip = g.getClip();

        Polygon plg = getPlg(size);

        g.setColor(col);
        g.fillPolygon(plg);

        g.setClip(new Rectangle(0, 0, size.width, size.height / 2).intersection(clip.getBounds()));
        g.setColor(Colors.bright(col, 1.15f));
        g.drawPolygon(plg);


        g.setClip(new Rectangle(0, size.height / 2, size.width, size.height / 2).intersection(clip.getBounds()));
        g.setColor(Colors.bright(col, 0.85f));
        g.drawPolygon(plg);

        g.setClip(clip);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

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
