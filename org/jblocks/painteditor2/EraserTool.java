package org.jblocks.painteditor2;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author ZeroLuck
 */
class EraserTool extends PaintTool implements MouseListener, MouseMotionListener {

    public EraserTool(JPaintEditor edt) {
        super(edt);
    }

    @Override
    void install(JPaintCanvas can) {
        can.addMouseListener(this);
        can.addMouseMotionListener(this);
    }

    @Override
    void uninstall(JPaintCanvas can) {
        can.removeMouseListener(this);
        can.removeMouseMotionListener(this);
    }

    private PaintAction createAction(MouseEvent evt, boolean erase) {
        final JPaintEditor edt = getEditor();
        final int x = evt.getX();
        final int y = evt.getY();
        final int r = edt.getLineWidthHeight();
        final boolean doErase = erase;
        return new PaintAction() {

            @Override
            public void draw(BufferedImage img, Graphics2D g) {
                g.setStroke(new BasicStroke(1));
                if (!doErase) {
                    g.setColor(Color.BLACK);
                    g.drawOval(x - r / 2, y - r / 2, r, r);
                } else {

                    Composite c = g.getComposite();
                    g.setComposite(
                            AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                    g.fillOval(x  - r / 2, y - r /2, r, r);
                    g.setComposite(c);
                }
            }
        };
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        JPaintEditor edt = getEditor();
        edt.addAction(createAction(me, true));
        edt.setPreviewAction(createAction(me, false));
        edt.paintUpdate();
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

    @Override
    public void mouseDragged(MouseEvent me) {
        JPaintEditor edt = getEditor();
        edt.addAction(createAction(me, true));
        edt.setPreviewAction(createAction(me, false));
        edt.paintUpdate();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        JPaintEditor edt = getEditor();
        edt.setPreviewAction(createAction(me, false));
        edt.paintUpdate();
    }
}
