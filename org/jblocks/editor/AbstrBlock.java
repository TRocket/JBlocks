package org.jblocks.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 * An abstract class for blocks. <br />
 * It handles block inputs and other things. <br />
 * The background color is used as the color for the whole block. <br />
 * <br />
 * 
 * @author ZeroLuck
 */
public abstract class AbstrBlock extends JComponent {

    private JScriptPane pane;

    /**
     * @throws NullPointerException if p is null.
     * @param p the script pane for this block.
     */
    public AbstrBlock(JScriptPane p) {
        if (p == null) {
            throw new NullPointerException();
        }
        this.pane = p;

        this.setLayout(null);
        this.setOpaque(true);
        this.setBackground(Color.GREEN);
        this.setBorder(null);
    }

    @Override
    public Dimension getPreferredSize() {
        if (!isValid()) {
            doLayout();
        }
        return super.getPreferredSize();
    }

    /**
     * 
     * This should be implemented to paint the block's border. <br />
     * @see #getBorderInsets(int, int) 
     */
    public abstract void drawBorder(Graphics g);

    /**
     * This should be implemented to return the insets of the block's border <br />
     * for a specified size. <br />
     * 
     * @param width the block's width
     * @param height the block's height
     * @return the insets of the border.
     */
    public abstract Insets getBorderInsets(int width, int height);

    /* Settings... */
    private static final int MAX_BLOCK_WIDTH = 150;
    private static final int INPUT_X_PADDING = 7;
    private static final int INPUT_Y_PADDING = 5;

    @Override
    public void doLayout() {
        int w = 0, h = 0;

        Component[] components = getComponents();

        int idx = 0;
        int yoff = 0, xoff = 0;
        while (idx < components.length) {
            int lineH = 0, lineW = 0;
            int i = idx;

            while (i < components.length && xoff < MAX_BLOCK_WIDTH) {
                Component comp = components[i++];
                Dimension size = comp.getPreferredSize();
                if (size.height > lineH) {
                    lineH = size.height;
                }
                xoff += size.width + INPUT_X_PADDING;
            }
            lineW = xoff;
            xoff = 0;

            while (idx < components.length && xoff < MAX_BLOCK_WIDTH) {
                Component comp = components[idx++];
                Dimension size = comp.getPreferredSize();
                comp.setSize(size);

                comp.setLocation(xoff, yoff + (lineH / 2 - size.height / 2));

                xoff += size.width + INPUT_X_PADDING;
            }

            if (lineW > w) {
                w = lineW;
            }
            if (idx < components.length) {
                yoff += INPUT_Y_PADDING;
            }
            yoff += lineH;

        }
        h = yoff;

        Insets border = getBorderInsets(w, h);
        
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            int x = comp.getX() + border.left;
            int y = comp.getY() + border.top;
            comp.setLocation(x, y);
        }
        w += border.left + border.right;
        h += border.top + border.bottom;
        setPreferredSize(new Dimension(w, h));

    }

    @Override
    public void paintComponent(Graphics g) {
        Insets border = getBorderInsets(getWidth(), getHeight());
        Color back = getBackground();

        Dimension size = getSize();
        Rectangle rect = new Rectangle(
                border.left,
                border.top,
                size.width - border.left - border.right,
                size.height - border.top - border.bottom);

        g.setColor(back);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);

        drawBorder(g);
    }
}
