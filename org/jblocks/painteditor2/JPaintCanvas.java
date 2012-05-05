package org.jblocks.painteditor2;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 *
 * @author ZeroLuck
 */
final class JPaintCanvas extends JPanel {

    private BufferedImage img;
    private float zoom = 1;

    /**
     * 
     * @throws IllegalArgumentException - if 'img' is null.
     * @param img - the initial image.
     * @see #setImage(java.awt.image.BufferedImage) 
     */
    public JPaintCanvas(BufferedImage img) {
        setImage(img);
    }

    /**
     * Returns the image which this JPaintCanvas displays. <br />
     * 
     * @return - the image of this JPaintCanvas.
     */
    protected BufferedImage getImage() {
        return this.img;
    }

    /**
     * Updates the image on the screen. <br />
     * This should be called when the image was changed. <br />
     */
    protected void updateImage() {
        repaint();
    }

    /**
     * Sets the image of this JPaintCanvas. <br />
     * You need to call <code>updateImage()</code> after that. <br />
     * 
     * @throws IllegalArgumentException - if 'img' is null.
     * @param img - the new image to display.
     * @see #updateImage()
     * @see #getImage() 
     */
    protected void setImage(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("'img' is null");
        }
        this.img = img;
    }

    /**
     * Sets the zoom of this JPaintCanvas. <br />
     * 
     * @see #getZoom()
     * @param zoom - the new zoom.
     */
    protected void setZoom(float zoom) {
        this.zoom = zoom;
        Container c = getParent();
        c.invalidate();
        c.validate();
    }

    /**
     * Returns the zoom of this JPaintCanvas. <br />
     * 
     * @return - the zoom of this JPaintCanvas.
     * @see #setZoom(float) 
     */
    protected float getZoom() {
        return zoom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) (img.getWidth() * zoom), (int) (img.getHeight() * zoom));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        final int FW = (int) (5 * zoom);
        final int FH = (int) (5 * zoom);
        final int W = (int) (img.getWidth() * zoom);
        final int H = (int) (img.getHeight() * zoom);


        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        g.setColor(Color.LIGHT_GRAY);
        int cnt = 0;
        for (int y = 0; y < H; y += FH, cnt++) {
            for (int x = cnt % 2 == 0 ? 0 : FW; x < W; x += FW * 2) {
                g.fillRect(x, y, FW, FH);
            }
        }
        g.drawImage(img, 0, 0, W, H, this);
    }

    /**
     * <p>
     *     Divides the cordinate of the event with the zoom.
     * </p>
     * {@inheritDoc}
     */
    @Override
    protected void processMouseMotionEvent(MouseEvent evt) {
        Point p = evt.getPoint();
        evt.translatePoint(-p.x, -p.y);
        p.x = (int) (p.x / zoom);
        p.y = (int) (p.y / zoom);
        evt.translatePoint(p.x, p.y);
        super.processMouseMotionEvent(evt);
    }

    /**
     * <p>
     *     Divides the cordinate of the event with the zoom.
     * </p>
     * {@inheritDoc}
     */
    @Override
    protected void processMouseEvent(MouseEvent evt) {
        Point p = evt.getPoint();
        evt.translatePoint(-p.x, -p.y);
        p.x = (int) (p.x / zoom);
        p.y = (int) (p.y / zoom);
        evt.translatePoint(p.x, p.y);
        super.processMouseEvent(evt);
    }
}
