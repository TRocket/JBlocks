package org.jblocks.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * The JZoomChooser is a swing component for choosing zoom. <br />
 * I made it for TRocket's paint-editor. <br />
 * <br />
 * - You can add listeners. <br />
 * - You can set the initial value <br />
 * - You can set the count of the "stairs" <br />
 * 
 * @version 1.0
 * @author ZeroLuck
 */
public class JZoomChooser extends JComponent {

    public static interface ZoomChangedListener {

        void zoomChanged(JZoomChooser ch, int newZoom);
    }
    // <global>
    private static BufferedImage zoomIn;
    private static BufferedImage zoomOut;
    private static final int X_SIZE = 5;
    private static final int Y_SIZE = 15;
    // <member>
    private int value = 0;
    private int cnt = 5;
    private ArrayList<ZoomChangedListener> clist = new ArrayList<ZoomChangedListener>();

    static {
        try {
            zoomIn = ImageIO.read(JZoomChooser.class.getResource("../res/zoom-in.png"));
            zoomOut = ImageIO.read(JZoomChooser.class.getResource("../res/zoom-out.png"));
        } catch (IOException io) {
            throw new ExceptionInInitializerError(io);
        }
    }

    /**
     * creates a new JZoomChooser with the initial value 0 and 5 steps.
     */
    public JZoomChooser() {
        this(0, 5);
    }

    /**
     * Adds the listener to this component. <br />
     * A event is fired when a change of the zoom-value happens. <br />
     * 
     * @see #removeZoomChangedListener(org.jblocks.gui.JZoomChooser.ZoomChangedListener) 
     * @param list - the Listener to add.
     */
    public void addZoomChangedListener(ZoomChangedListener list) {
        if (list == null) {
            throw new IllegalArgumentException("listener is null!");
        }
        clist.add(list);
    }

    /**
     * Removes the listener. <br />
     * 
     * @param list - the Listener to remove.
     */
    public void removeZoomChangedListener(ZoomChangedListener list) {
        if (list == null) {
            throw new IllegalArgumentException("listener is null!");
        }
        clist.remove(list);
    }

    private boolean insideZoomIn(Point p) {
        Dimension size = getPreferredSize();
        return new Rectangle(size.width - zoomIn.getWidth(), 0,
                zoomIn.getWidth(), zoomIn.getHeight()).contains(p);
    }

    private boolean insideZoomOut(Point p) {
        return new Rectangle(0, 0,
                zoomOut.getWidth(), zoomOut.getHeight()).contains(p);
    }

    /**
     * 
     * @param val - the initial value.
     * @param c - the count of "steps" this chooser should have.
     */
    public JZoomChooser(int val, int c) {
        value = val;
        cnt = c;
        setOpaque(true);

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent evt) {
                if (insideZoomIn(evt.getPoint()) || insideZoomOut(evt.getPoint())) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                if (insideZoomIn(evt.getPoint())) {
                    if (value + 1 < cnt) {
                        value++;
                        fireZoomChangedEvent();
                        repaint();
                    }
                } else if (insideZoomOut(evt.getPoint())) {
                    if (value - 1 >= 0) {
                        value--;
                        fireZoomChangedEvent();
                        repaint();
                    }
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(zoomIn.getWidth() + zoomOut.getWidth() + cnt * X_SIZE,
                Math.max(Y_SIZE, Math.max(zoomIn.getHeight(), zoomOut.getHeight())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getPreferredSize();
        Shape clip = g.getClip();
        g.setClip(0, 0, size.width, size.height);

        g.drawImage(zoomOut, 0, 0, null);
        g.drawImage(zoomIn, size.width - zoomIn.getWidth(), 0, null);

        int x = zoomOut.getWidth();
        for (int i = 0; i < cnt; i++, x += X_SIZE) {
            if (i <= value) {
                g.setColor(Color.CYAN);
            } else {
                g.setColor(Color.GRAY);
            }
            g.fillRect(x, Y_SIZE - (int) (((double) (Y_SIZE - 3) / cnt) * i) - 3, X_SIZE - 2, Y_SIZE);
        }

        g.setClip(clip);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }
    
    private void fireZoomChangedEvent() {
        for (ZoomChangedListener list : clist) {
            list.zoomChanged(this, value);
        }
    }
}
