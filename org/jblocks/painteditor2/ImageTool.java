package org.jblocks.painteditor2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author ZeroLuck
 */
public class ImageTool extends PaintTool implements MouseListener, MouseMotionListener {

    private final BufferedImage img;
    private int x, y;
    private boolean doDrag = false;
    private Point dragOff;

    public ImageTool(JPaintEditor edt, BufferedImage img) {
        super(edt);
        this.img = img;
    }

    @Override
    void install(JPaintCanvas can) {
        can.addMouseMotionListener(this);
        can.addMouseListener(this);

        x = can.getWidth() / 2 - img.getWidth() / 2;
        y = can.getHeight() / 2 - img.getHeight() / 2;
         
        JPaintEditor edt = getEditor();
        edt.setPreviewAction(getPreview(x, y));
        edt.paintUpdate();
    }

    @Override
    void uninstall(JPaintCanvas can) {
        end();

        can.removeMouseMotionListener(this);
        can.removeMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        Point loc = me.getPoint();
        Rectangle rect = new Rectangle(x, y, img.getWidth(), img.getHeight());
        if (rect.contains(loc)) {
            dragOff = new Point(loc.x - x, loc.y - y);
            doDrag = true;
        } else {
            end();
            getEditor().setTool(null);
        }
    }

    private PaintAction getPreview(final int theX, final int theY) {
        return new PaintAction() {

            @Override
            public void draw(BufferedImage buf, Graphics2D g) {
                g.setColor(Color.BLACK);
                g.drawRect(theX, theY, img.getWidth(), img.getHeight());
                g.drawImage(img, theX, theY, null);
            }
        };
    }

    private void end() {
        final int theX = x;
        final int theY = y;
        JPaintEditor edt = getEditor();
        edt.addAction(new PaintAction() {

            @Override
            public void draw(BufferedImage buf, Graphics2D g) {
                g.drawImage(img, theX, theY, null);
            }
        });
        edt.paintUpdate();
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        doDrag = false;
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (doDrag) {
            x = me.getX() - dragOff.x;
            y = me.getY() - dragOff.y;
            JPaintEditor edt = getEditor();
            edt.setPreviewAction(getPreview(x, y));
            edt.paintUpdate();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }
}
