package org.jblocks.editor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.SwingUtilities;

/**
 *
 * @author ZeroLuck
 */
class JReporterBlock extends AbstrBlock {

    /**
     * See {@link AbstrBlock#AbstrBlock(org.jblocks.editor.BlockModel) }
     */
    public JReporterBlock(BlockModel model) {
        super(model);
    }

    @Override
    protected void paintBlockBorder(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Color col = getBackground();
        Shape clip = g.getClip();

        g.setColor(col);
        g.fillRoundRect(0, 0, size.width, size.height, size.height / 2, size.height / 2);

        g.setColor(Colors.bright(col, 1.15f));
        g.setClip(new Rectangle(0, 0, size.width, size.height / 2).intersection(clip.getBounds()));
        g.drawRoundRect(0, 0, size.width - 1, size.height - 1, size.height / 2, size.height / 2);

        g.setColor(Colors.bright(col, 0.85f));
        g.setClip(new Rectangle(0, size.height / 2, size.width, size.height / 2 + 1).intersection(clip.getBounds()));
        g.drawRoundRect(0, 0, size.width - 1, size.height - 1, size.height / 2, size.height / 2);

        g.setClip(clip);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

    }

    @Override
    protected Insets getBorderInsets(int width, int height) {
        return new Insets(2, height / 4, 2, height / 4);
    }

    @Override
    public boolean contains(int x, int y) {
        Dimension size = getSize();

        RoundRectangle2D.Float rect = new RoundRectangle2D.Float(0, 0, size.width, size.height, size.height / 4, size.height / 4);

        return rect.contains(x, y);
    }

    @Override
    protected void pressedEvent(MouseEvent evt) {
        if (SwingUtilities.isLeftMouseButton(evt)) {
            Container parent = getParent();
            Container pane = getScriptPane();
            if (parent != pane) {
                Point loc = JScriptPane.getLocationOnScriptPane(this);

                if (parent instanceof AbstrInput) {
                    ((AbstrInput) parent).reset();
                }
                parent.remove(this);

                setLocation(loc);
                pane.add(this);
                layoutRoot();
            }
        }
        super.pressedEvent(evt);
    }

    @Override
    protected void releasedEvent(MouseEvent evt) {
        AbstrInput inp = AbstrInput.findInput(getScriptPane(), getBounds(), this);

        if (inp != null) {
            getParent().remove(this);
            inp.setInput(this);
            inp.setBorderEnabled(false);
            layoutRoot();
        }
    }
}
