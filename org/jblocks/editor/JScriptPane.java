package org.jblocks.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.jblocks.JBlocks;

/**
 *
 * A ScriptPane for the BlockEditor. <br />
 * 
 * @version 0.1
 * @author ZeroLuck
 */
public class JScriptPane extends JPanel {

    private static BufferedImage scriptpane;

    static {
        try {
            scriptpane = ImageIO.read(JBlocks.class.getResourceAsStream("res/scriptpane.png"));
        } catch (IOException ex) {
            throw new java.lang.ExceptionInInitializerError(ex);
        }
    }

    public JScriptPane() {
        setBackground(Color.WHITE);
        setLayout(null);
    }
    private static final int CLEANUP_LEFT = 5;
    private static final int CLEANUP_BOTTOM = 5;
    private static final int CLEANUP_TOP = 5;

    /**
     * 
     * Layouts the scripts like in Scratch.
     */
    public void cleanup() {
        doLayout();
        int y = CLEANUP_TOP;

        for (Component c : getComponents()) {
            Dimension p = c.getSize();
            c.setLocation(CLEANUP_LEFT, y);

            y += p.height + CLEANUP_BOTTOM;
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        for (Component c : getComponents()) {
            if (!c.getSize().equals(c.getPreferredSize())) {
                c.setSize(c.getPreferredSize());
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int w = 0, h = 0;
        for (Component c : getComponents()) {
            Point p = c.getLocation();
            if (p.x > w) {
                w = p.x;
            }
            if (p.y > h) {
                h = p.y;
            }
        }

        return new Dimension(w + 100, h + 100);
    }

    @Override
    public void paintComponent(Graphics g) {
        BufferedImage back = scriptpane;
        Rectangle clip = g.getClipBounds();
        int w = back.getWidth();
        int h = back.getHeight();

        for (int x = clip.x - (clip.x % w); x < (clip.x + clip.width); x += w) {
            for (int y = clip.y - (clip.y % h); y < (clip.y + clip.height); y += h) {
                g.drawImage(back, x, y, this);
            }
        }
    }
}
