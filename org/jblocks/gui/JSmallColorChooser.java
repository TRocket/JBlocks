package org.jblocks.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JComponent;
import org.jblocks.JBlocks;

/**
 *
 * A small color chooser for the paint-editor. <br />
 * (Should be the default, but the should be an ability to open the <br />
 *          {@link javax.swing.JColorChooser} in an JInternalFrame too) <br />
 * <br />
 * You can switch between two different color-chooser styles: <br />
 *  (with the <code>setStyle(int)</code> method) <br />
 * <ol>
 *    <li><code>JSmallColorChooser.GRADIENT</code> - gradient color chooser</li>
 *    <li><code>JSmallColorChooser.RECTANGULAR</code> - there are rectangels which can be selected</li>
 * </ol>
 * <br />
 * You can add ColorChangedListeners: <br />
 * <ul>
 *    <li><code>addColorChangedListener(ColorChangedListener)</code></li>
 *    <li><code>removeColorChangedListener(ColorChangedListener)</code></li>
 * </ul>
 * 
 * @version 1.0
 * @author ZeroLuck
 */
public class JSmallColorChooser extends JComponent {

    public static interface ColorChangedListener {

        void colorChanged(JSmallColorChooser ch, Color c);
    }
    // <global>
    private static final int RW = 15;
    private static final int RH = 15;
    private static final int X_CNT = 10;
    private static final int Y_CNT = 10;
    public static final int GRADIENT = 0;
    public static final int RECTANGULAR = 1;
    private static final BufferedImage gradient = JBlocks.getImage("gradient.png");
    private static final Color[] colors = new Color[]{
        Color.RED,
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.YELLOW,
        Color.ORANGE,
        Color.MAGENTA,
        Color.CYAN,
        Color.PINK,
        Color.WHITE
    };
    // <member>
    private int style = RECTANGULAR;
    private Point gsel = new Point(0, 0);
    private Point sel = new Point(0, 0);
    private ArrayList<ColorChangedListener> clist = new ArrayList<ColorChangedListener>();

    /**
     * creates a new color-chooser. <br />
     */
    public JSmallColorChooser() {
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent evt) {
                if (style == GRADIENT) {
                    Point p = new Point(evt.getPoint());
                    if (evt.getX() < 0) {
                        p.x = 0;
                    }
                    if (evt.getY() < 0) {
                        p.y = 0;
                    }
                    if (evt.getX() >= RW * X_CNT - 1) {
                        p.x = RW * X_CNT - 1;
                    }
                    if (evt.getY() >= RH * Y_CNT - 1) {
                        p.y = RH * Y_CNT - 1;
                    }

                    sel = p;
                    int col = gradient.getRGB(
                            (int) ((double) p.x / ((double) (RW * X_CNT) / gradient.getWidth())),
                            (int) ((double) p.y / ((double) (RH * Y_CNT) / gradient.getHeight())));
                    colorChanged(new Color(col));
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                if (style == RECTANGULAR) {
                    int x = evt.getX() / RW;
                    int y = evt.getY() / RH;
                    if (x >= X_CNT) {
                        x = X_CNT - 1;
                    }
                    if (y >= Y_CNT) {
                        y = Y_CNT - 1;
                    }
                    gsel.x = x;
                    gsel.y = y;
                    repaint();
                }
            }
        });
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                if (style == RECTANGULAR) {
                    int x = evt.getX() / RW;
                    int y = evt.getY() / RH;
                    Color col = bright(colors[x], ((double) 1 / Y_CNT) * y);
                    colorChanged(col);
                    sel.x = x;
                    sel.y = y;
                    repaint();
                } else {
                    Point p = evt.getPoint();
                    sel = p;
                    int col = gradient.getRGB(
                            (int) ((double) p.x / ((double) (RW * X_CNT) / gradient.getWidth())),
                            (int) ((double) p.y / ((double) (RH * Y_CNT) / gradient.getHeight())));
                    colorChanged(new Color(col));
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                gsel.x = -1;
                gsel.y = -1;
                repaint();
            }
        });

    }

    /**
     * Adds a ColorChangedListener. <br />
     * An Event will be fired when a different color was selected. <br />
     * 
     * @param list - the Listener to add.
     * @throws IllegalArgumentException - if the listener is null.
     * @see #removeColorChangedListener(org.jblocks.gui.JSmallColorChooser.ColorChangedListener) 
     */
    public void addColorChangedListener(ColorChangedListener list) {
        if (list == null) {
            throw new IllegalArgumentException("listener is null!");
        }
        clist.add(list);
    }

    /**
     * Removes a listener from this component. <br />
     * 
     * @param list - the Listener to remove.
     */
    public void removeColorChangedListener(ColorChangedListener list) {
        if (list == null) {
            throw new IllegalArgumentException("listener is null!");
        }
        clist.remove(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(RW * X_CNT, RH * Y_CNT);
    }

    /**
     * Sets the style of this color-chooser. <br />
     * repaint() is called! <br />
     * 
     * @param s - the new style.
     * @see #getStyle()
     * @see #GRADIENT
     * @see #RECTANGULAR
     */
    public void setStyle(int s) {
        style = s;
        repaint();
    }

    /**
     * Returns the style of this color-chooser. <br />
     * 
     * @return - the style of this color-chooser.
     * @see #getStyle() 
     */
    public int getStyle() {
        return style;
    }

    private static Color bright(Color c, double f) {
        return new Color(Math.min((int) (c.getRed() * f), 255),
                Math.min((int) (c.getGreen() * f), 255),
                Math.min((int) (c.getBlue() * f), 255));
    }

    private void colorChanged(Color c) {
        for (ColorChangedListener list : clist) {
            list.colorChanged(this, c);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        if (style == RECTANGULAR) {
            for (int x = 0; x < X_CNT; x++) {
                Color col = colors[x];
                for (int y = 0; y < Y_CNT; y++) {
                    g.setColor(bright(col, ((double) 1 / Y_CNT) * y));
                    g.fillRect(x * RW, y * RH, RW, RH);
                }
            }
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(gsel.x * RW, gsel.y * RH, RW, RH);
            g.setColor(Color.WHITE);
            g.drawRect(sel.x * RW, sel.y * RH, RW, RH);
        } else if (style == GRADIENT) {
            Object obj = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(gradient, 0, 0, RW * X_CNT, RH * Y_CNT, null);

            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(sel.x - 5, sel.y, sel.x + 5, sel.y);
            g.drawLine(sel.x, sel.y - 5, sel.x, sel.y + 5);

            if (obj != null) {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, obj);
            }
        }
    }
}
