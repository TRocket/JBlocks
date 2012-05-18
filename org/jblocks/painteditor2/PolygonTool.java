package org.jblocks.painteditor2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

/**
 *
 * @author ZeroLuck
 */
public class PolygonTool extends PaintTool implements MouseListener, MouseMotionListener {

    private Polygon p;
    private Point prev;

    public PolygonTool(JPaintEditor edt) {
        super(edt);
    }

    @Override
    void install(JPaintCanvas can) {
        p = new Polygon();

        can.addMouseMotionListener(this);
        can.addMouseListener(this);
    }

    @Override
    void uninstall(JPaintCanvas can) {
        p = null;

        can.removeMouseMotionListener(this);
        can.removeMouseListener(this);
    }

    private PaintAction createPolygon(Point add) {
        JPaintEditor edt = getEditor();

        final Polygon polg = new Polygon();
        final boolean fill = getEditor().getFill();
        final Color col = getEditor().getColorA();
        final Stroke s = edt.getLineStroke();
        for (int i = 0; i < p.npoints; i++) {
            polg.addPoint(p.xpoints[i], p.ypoints[i]);
        }
        if (add != null) {
            polg.addPoint(add.x, add.y);
        }

        return new PaintAction() {

            @Override
            public void draw(BufferedImage img, Graphics2D g) {
                g.setColor(col);
                g.setStroke(s);
                if (fill) {
                    g.fillPolygon(polg);
                } else {
                    g.drawPolygon(polg);
                }
            }
        };
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (SwingUtilities.isRightMouseButton(me)) {
            getEditor().addAction(createPolygon(null));
            getEditor().paintUpdate();
            p.reset();
        } else {
            p.addPoint(me.getX(), me.getY());
            prev = null;
            getEditor().setPreviewAction(createPolygon(null));
            getEditor().paintUpdate();
        }
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
        prev = me.getPoint();
        getEditor().setPreviewAction(createPolygon(prev));
        getEditor().paintUpdate();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        prev = me.getPoint();
        getEditor().setPreviewAction(createPolygon(prev));
        getEditor().paintUpdate();
    }
}
