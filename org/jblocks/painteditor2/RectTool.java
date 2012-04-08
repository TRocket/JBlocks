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
class RectTool extends PaintTool implements MouseListener, MouseMotionListener {

    private int startX;
    private int startY;

    public RectTool(JPaintEditor edt) {
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
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        startX = me.getX();
        startY = me.getY();
    }

    private PaintAction createAction(MouseEvent evt) {
        final JPaintEditor edt = getEditor();
        final Color col = edt.getColorA();
        final Stroke s = edt.getLineStroke();
        final int xs = startX;
        final int ys = startY;
        final int xe = evt.getX();
        final int ye = evt.getY();
        final boolean fill = edt.getFill();
        return new PaintAction() {

            @Override
            public void draw(BufferedImage img, Graphics2D g) {
                g.setColor(col);
                g.setStroke(s);
                if (!fill) {
                    g.drawRect(xs, ys, xe - xs, ye - ys);
                } else {
                    g.fillRect(xs, ys, xe - xs, ye - ys);
                }
            }
        };
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        JPaintEditor edt = getEditor();
        edt.addAction(createAction(me));
        edt.paintUpdate();
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        JPaintEditor edt = getEditor();
        edt.setPreviewAction(createAction(me));
        edt.paintUpdate();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }
}
