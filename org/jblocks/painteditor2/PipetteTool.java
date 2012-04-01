package org.jblocks.painteditor2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author ZeroLuck
 */
class PipetteTool extends PaintTool implements MouseListener, MouseMotionListener {

    private Image pipette;

    public PipetteTool(JPaintEditor edt, Image pipette) {
        super(edt);
        if (pipette == null) {
            throw new IllegalArgumentException("'pipette' is null!");
        }
        this.pipette = pipette;
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

    private PaintAction createAction(MouseEvent evt) {
        final int x = evt.getX();
        final int y = evt.getY();
        return new PaintAction() {

            @Override
            public void draw(BufferedImage img, Graphics2D g) {
                g.drawImage(pipette, x, y - 20, null);
            }
        };
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        JPaintEditor edt = getEditor();
        edt.setColorA(
                new Color(edt.getCanvas().getImage().getRGB(me.getX(), me.getY())));
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
        edt.setPreviewAction(createAction(me));
        edt.paintUpdate();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        JPaintEditor edt = getEditor();
        edt.setPreviewAction(createAction(me));
        edt.paintUpdate();
    }
}
