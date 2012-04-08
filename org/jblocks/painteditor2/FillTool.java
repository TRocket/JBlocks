/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.painteditor2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author ZeroLuck
 */
class FillTool extends PaintTool implements MouseListener, MouseMotionListener {

    private Image fillImg;

    public FillTool(JPaintEditor edt, Image fillImg) {
        super(edt);
        if (fillImg == null) {
            throw new IllegalArgumentException("'fillImg' is null!");
        }
        this.fillImg = fillImg;
    }

    /** 
     * Fills the selected pixel and all surrounding pixels of the same color with the fill color. 
     * @param img image on which operation is applied 
     * @param fillColor color to be filled in 
     * @param loc location at which to start fill 
     * @throws IllegalArgumentException if loc is out of bounds of the image 
     */
    public static void floodFill(BufferedImage img, Color fillColor, Point loc) {
        if (loc.x < 0 || loc.x >= img.getWidth() || loc.y < 0 || loc.y >= img.getHeight()) {
            throw new IllegalArgumentException();
        }

        WritableRaster raster = img.getRaster();
        int[] fill =
                new int[]{fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(),
            fillColor.getAlpha()};
        int[] old = raster.getPixel(loc.x, loc.y, new int[4]);

        // Checks trivial case where loc is of the fill color  
        if (isEqualRgba(fill, old)) {
            return;
        }

        floodLoop(raster, loc.x, loc.y, fill, old);
    }

    // Recursively fills surrounding pixels of the old color  
    private static void floodLoop(WritableRaster raster, int x, int y, int[] fill, int[] old) {
        Rectangle bounds = raster.getBounds();
        int[] aux = {255, 255, 255, 255};

        // finds the left side, filling along the way  
        int fillL = x;
        do {
            raster.setPixel(fillL, y, fill);
            fillL--;
        } while (fillL >= 0 && isEqualRgba(raster.getPixel(fillL, y, aux), old));
        fillL++;

        // find the right right side, filling along the way  
        int fillR = x;
        do {
            raster.setPixel(fillR, y, fill);
            fillR++;
        } while (fillR < bounds.width - 1 && isEqualRgba(raster.getPixel(fillR, y, aux), old));
        fillR--;

        // checks if applicable up or down  
        for (int i = fillL; i <= fillR; i++) {
            if (y > 0 && isEqualRgba(raster.getPixel(i, y - 1, aux), old)) {
                floodLoop(raster, i, y - 1,
                        fill, old);
            }
            if (y < bounds.height - 1 && isEqualRgba(raster.getPixel(i, y + 1, aux), old)) {
                floodLoop(
                        raster, i, y + 1, fill, old);
            }
        }
    }

    // Returns true if RGBA arrays are equivalent, false otherwise  
    // Could use Arrays.equals(int[], int[]), but this is probably a little faster...  
    private static boolean isEqualRgba(int[] pix1, int[] pix2) {
        return pix1[0] == pix2[0] && pix1[1] == pix2[1] && pix1[2] == pix2[2] && pix1[3] == pix2[3];
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

    private PaintAction createFillAction(MouseEvent evt) {
        final JPaintEditor edt = getEditor();
        final int x = evt.getX();
        final int y = evt.getY();
        final Color col = edt.getColorA();
        return new PaintAction() {

            @Override
            public void draw(BufferedImage img, Graphics2D g) {
                floodFill(img, col, new Point(x, y));
            }
        };
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        JPaintEditor edt = getEditor();
        edt.addAction(createFillAction(me));
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
    }

    private PaintAction createAction(MouseEvent evt) {
        final int x = evt.getX();
        final int y = evt.getY();
        return new PaintAction() {

            @Override
            public void draw(BufferedImage img, Graphics2D g) {
                g.drawImage(fillImg, x, y - fillImg.getHeight(null), null);
            }
        };
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        JPaintEditor edt = getEditor();
        edt.setPreviewAction(createAction(me));
        edt.paintUpdate();
    }
}
