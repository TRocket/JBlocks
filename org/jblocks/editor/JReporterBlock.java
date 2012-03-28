package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author ZeroLuck
 */
class JReporterBlock extends AbstrBlock {

    public JReporterBlock(JScriptPane pane) {
        super(pane);
    }

    @Override
    protected void paintBlockBorder(Graphics grp) {
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
    protected void dragEvent(MouseEvent evt) {
        super.dragEvent(evt);
    }

    @Override
    protected void pressedEvent(MouseEvent evt) {
        Container parent = getParent();
        JScriptPane pane = getScriptPane();
        if (parent != pane) {
            if (parent instanceof AbstrInput) {
                ((AbstrInput) parent).reset();
            }
            parent.remove(this);
            
            setLocation(JScriptPane.getLocationOnScriptPane(this));
            pane.add(this);
            layoutRoot();
        }

        super.pressedEvent(evt);
    }

    @Override
    protected void releasedEvent(MouseEvent evt) {
        AbstrInput inp = AbstrInput.findInput(getScriptPane(),
                new Rectangle(JScriptPane.getLocationOnScriptPane(this),
                getSize()), this);

        if (inp != null) {
            getParent().remove(this);
            inp.setInput(this);
            inp.setBorderEnabled(false);
            layoutRoot();
        }
    }
}
