package org.jblocks.painteditor2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author ZeroLuck
 */
class BrushTool extends PaintTool implements MouseMotionListener, MouseListener {

    private int lastX;
    private int lastY;

    public BrushTool(JPaintEditor edt) {
        super(edt);
    }

    @Override
    void install(JPaintCanvas can) {
        can.addMouseMotionListener(this);
        can.addMouseListener(this);
    }

    @Override
    void uninstall(JPaintCanvas can) {
        can.removeMouseMotionListener(this);
        can.removeMouseListener(this);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        final JPaintEditor edt = getEditor();
        final Color col = edt.getColorA();
        final int xs = lastX;
        final int ys = lastY;
        final int xe = me.getX();
        final int ye = me.getY();
        final Stroke s = edt.getLineStroke();
        lastX = xe;
        lastY = ye;
        edt.addAction(new PaintAction() {

            @Override
            public void draw(BufferedImage img, Graphics2D g) {
                g.setColor(col);
                g.setStroke(s);
                g.drawLine(xs, ys, xe, ye);
            }
        });
        edt.paintUpdate();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        final JPaintEditor edt = getEditor();
        final Color col = edt.getColorA();
        final Stroke s = edt.getLineStroke();
        final int x = me.getX();
        final int y = me.getY();
        edt.setPreviewAction(new PaintAction() {

            @Override
            public void draw(BufferedImage img, Graphics2D g) {
                g.setColor(col);
                g.setStroke(s);
                g.drawLine(x, y, x, y);
            }
        });
        edt.paintUpdate();
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        lastX = me.getX();
        lastY = me.getY();
        getEditor().setPreviewAction(null);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}
