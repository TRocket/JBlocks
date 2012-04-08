package org.jblocks.painteditor2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author ZeroLuck
 */
class StampTool extends PaintTool implements MouseListener, MouseMotionListener {

    private int startX;
    private int startY;
    private int width;
    private int height;
    private boolean select = true;
    private BufferedImage stamp;

    public StampTool(JPaintEditor edt) {
        super(edt);
    }

    @Override
    void install(JPaintCanvas can) {
        can.addMouseMotionListener(this);
        can.addMouseListener(this);
        select = true;
    }

    @Override
    void uninstall(JPaintCanvas can) {
        can.removeMouseMotionListener(this);
        can.removeMouseListener(this);
        if (stamp != null) {
            stamp.flush();
            stamp = null;
        }
    }

    private PaintAction createSelectAction(final boolean rect) {
        final int xs = startX;
        final int ys = startY;
        final int w = width;
        final int h = height;
        return new PaintAction() {

            @Override
            public void draw(BufferedImage img, Graphics2D g) {
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(1));
                if (rect) {
                    g.drawRect(xs, ys, w, h);
                    g.drawLine(xs - 5 + w, ys + h, xs + 5 + w, ys + h);
                    g.drawLine(xs + w, ys - 5 + h, xs + w, ys + 5 + h);
                } else {
                    g.drawLine(xs - 5, ys, xs + 5, ys);
                    g.drawLine(xs, ys - 5, xs, ys + 5);
                }
            }
        };
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (!select) {
            JPaintEditor edt = getEditor();
            edt.addAction(createStampAction(me));
            edt.paintUpdate();
        }
    }

    private PaintAction createStampAction(MouseEvent evt) {
        final int x = evt.getX();
        final int y = evt.getY();
        final BufferedImage stampImg = stamp;
        return new PaintAction() {

            @Override
            public void draw(BufferedImage img, Graphics2D g) {
                g.drawImage(stampImg, x, y, null);
            }
        };
    }
    
    private BufferedImage deepSubimage(BufferedImage img, int x, int y, int w, int h) {
        BufferedImage n = new BufferedImage(w, h, img.getType());
        for (int i = x;i < x + w;i++) {
            for (int j = y; j < y + h; j++) {
                n.setRGB(i - x, j - y, img.getRGB(i, j));
            }
        }
        return n;
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (select) {
            select = false;
            JPaintEditor edt = getEditor();
            edt.setPreviewAction(null);
            edt.paintUpdate();
            stamp = deepSubimage(edt.getCanvas().getImage(), startX, startY, width, height);
            edt.setPreviewAction(createStampAction(me));
            edt.paintUpdate();
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (select) {
            JPaintEditor edt = getEditor();
            width = me.getX() - startX;
            height = me.getY() - startY;
            edt.setPreviewAction(createSelectAction(true));
            edt.paintUpdate();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if (select) {
            JPaintEditor edt = getEditor();
            startX = me.getX();
            startY = me.getY();
            edt.setPreviewAction(createSelectAction(false));
            edt.paintUpdate();
        } else {
            JPaintEditor edt = getEditor();
            edt.setPreviewAction(createStampAction(me));
            edt.paintUpdate();
        }
    }
}
