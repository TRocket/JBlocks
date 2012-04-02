package org.jblocks.painteditor2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

/**
 *
 * @author ZeroLuck
 */
public class JColorSwitcher extends JComponent {

    // <global>
    private static final int RECT_W_H = 20;
    private static final int RECT_B_OFFSET = 10;
    // <member>
    private Color colA;
    private Color colB;
    private Rectangle rectA;
    private Rectangle rectB;

    /**
     * Creates a new JColoChooser with <u>color-A = BLACK</u> 
     * and <u>color-B = RED</u>. <br />
     * 
     * @see #setColorA(java.awt.Color)
     * @see #setColorB(java.awt.Color) 
     */
    public JColorSwitcher() {
        setOpaque(true);

        colA = Color.BLACK;
        colB = Color.RED;
        rectA = new Rectangle(0, 0, RECT_W_H, RECT_W_H);
        rectB = new Rectangle(RECT_B_OFFSET, RECT_B_OFFSET, RECT_W_H, RECT_W_H);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                if (rectA.contains(evt.getPoint())) {
                    switchColors();
                } else if (rectB.contains(evt.getPoint())) {
                    switchColors();
                }
            }
        }); 
    }

    public void switchColors() {
        Color tmp = colB;
        colB = colA;
        colA = tmp;
        repaint();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(RECT_B_OFFSET + RECT_W_H, RECT_B_OFFSET + RECT_W_H);
    }

    private void drawColorRect(Graphics g, Rectangle r, Color c) {
        fillTransparent(g, r.x, r.y, r.width, r.height);
        g.setColor(c);
        g.fillRect(r.x, r.y, r.width, r.height);

        g.setColor(Color.BLACK);
        g.drawRect(r.x, r.y, r.width, r.height);
    }

    private void fillTransparent(Graphics g, int rx, int ry, int rw, int rh) {
        Rectangle clip = g.getClipBounds();
        g.setClip(rx, ry, rw, rh);

        g.setColor(Color.WHITE);
        g.fillRect(rx, ry, rw, rh);

        final int FH = 5;
        final int FW = 5;

        g.setColor(Color.LIGHT_GRAY);
        int cnt = 0;
        for (int y = rx; y < rh; y += FH, cnt++) {
            for (int x = cnt % 2 == 0 ? 0 : FW; x < rw; x += FW * 2) {
                g.fillRect(x, y, FW, FH);
            }
        }

        g.setClip(clip);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        drawColorRect(g, rectA, colA);
        drawColorRect(g, rectB, colB);
    }

    /**
     * 
     * @throws IllefalArgumentException - if 'c' is null.
     * @param c - the new color-A for this JColorSwitcher.
     */
    public void setColorA(Color c) {
        colA = c;
        repaint();
    }

    /**
     * 
     * @throws IllefalArgumentException - if 'c' is null.
     * @param c - the new color-B for this JColorSwitcher.
     */
    public void setColorB(Color c) {
        colB = c;
        repaint();
    }

    /**
     * 
     * @return - the color-A of this JColorSwitcher.
     */
    public Color getColorA() {
        return colA;
    }

    /**
     * 
     * @return - the color-B of this JColorSwitcher.
     */
    public Color getColorB() {
        return colB;
    }
}
