package org.jblocks.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import org.jblocks.gui.JDragPane;

/**
 * An abstract class for blocks. <br />
 * It handles block inputs and other things. <br />
 * The background color is used as the color for the whole block. <br />
 * <br />
 * 
 * @author ZeroLuck
 */
public abstract class AbstrBlock extends JComponent {

    protected Point drag;

    public AbstrBlock() {
        this.setLayout(null);
        this.setOpaque(false);
        this.setBackground(new Color(0xD6900A)); // <- TEST
        this.setBorder(null);

        BlockMouseListener listener = new BlockMouseListener();
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);
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
    protected abstract void paintBlockBorder(Graphics g);

    /**
     * This should be implemented to return the insets of the block's border <br />
     * for a specified size. <br />
     * 
     * @param width the block's width
     * @param height the block's height
     * @return the insets of the border.
     */
    protected abstract Insets getBorderInsets(int width, int height);

    public JScriptPane getScriptPane() {
        Container cont = this;
        while ((cont = cont.getParent()) != null) {
            if (cont instanceof JScriptPane) {
                return (JScriptPane) cont;
            }
        }
        return null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public abstract boolean contains(int x, int y);

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    /* Settings... */
    private static final int MAX_BLOCK_WIDTH = 300;
    private static final int INPUT_X_PADDING = 7;
    private static final int INPUT_Y_PADDING = 5;

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        int w = 0, h = 0;

        Component[] components = getComponents();

        int idx = 0;
        int yoff = 2, xoff;
        while (idx < components.length) {
            int lineH = 0, lineW = 0;
            int i = idx;
            xoff = 2;

            while (i < components.length && xoff < MAX_BLOCK_WIDTH) {
                Component comp = components[i++];
                if (comp instanceof NewLineComponent) {
                    break;
                }
                Dimension size = comp.getPreferredSize();
                if (size.height > lineH) {
                    lineH = size.height;
                }
                xoff += size.width + INPUT_X_PADDING;
            }
            lineW = xoff;
            xoff = 2;

            while (idx < components.length && xoff < MAX_BLOCK_WIDTH) {
                Component comp = components[idx++];
                if (comp instanceof NewLineComponent) {
                    break;
                }
                Dimension size = comp.getPreferredSize();
                comp.setSize(size);

                comp.setLocation(xoff, yoff + (lineH / 2 - size.height / 2));

                xoff += size.width + INPUT_X_PADDING;
            }

            if (lineW > w) {
                w = lineW;
            }
            if (idx < components.length) {
                yoff += INPUT_Y_PADDING + lineH;
            } else {
                yoff += lineH;
            }

        }
        h = yoff + 2;

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

    /**
     * {@inheritDoc}
     */
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

        paintBlockBorder(g);
    }

    protected void toFront() {
        Container root = getScriptPane();
        if (getParent() == root) {
            root.remove(this);
            root.add(this, 0);
        }
    }

    protected void dragEvent(MouseEvent evt) {
        if (drag == null) { // shouldn't happen...
            return;
        }
        int newX = evt.getX() + getX() - drag.x;
        int newY = evt.getY() + getY() - drag.y;
        if (newX < 0) {
            newX = 0;
        }
        if (newY < 0) {
            newY = 0;
        }
        setLocation(newX, newY);

    }

    protected void moveEvent(MouseEvent evt) {
        drag = evt.getPoint();
        toFront();
    }

    protected void pressedEvent(MouseEvent evt) {
        drag = evt.getPoint();
        toFront();
    }

    protected void releasedEvent(MouseEvent evt) {
        drag = null;
    }

    /**
     * 
     * Layouts the whole ScriptPane. <br />
     */
    protected void layoutRoot() {
        getScriptPane().validate();
    }

    private static PuzzleAdapter findAdapter0(AbstrBlock b, Rectangle r, int t) {
        if (b instanceof Puzzle) {
            Puzzle p = (Puzzle) b;
            PuzzleAdapter[] adapters = p.getPuzzleAdapters();
            for (PuzzleAdapter a : adapters) {
                Rectangle clone = new Rectangle(a.bounds.x, a.bounds.y,
                        a.bounds.width, a.bounds.height);

                clone.x += a.block.getX();
                clone.y += a.block.getY();

                if (clone.intersects(r) && a.neighbour == null
                        && a.type == t) {
                    return a;
                }
            }
        }
        return null;
    }

    static PuzzleAdapter findAdapter(AbstrBlock b, PuzzleAdapter p, int t) {
        Component[] hats = b.getScriptPane().getComponents();
        Rectangle r = new Rectangle(p.bounds.x, p.bounds.y,
                p.bounds.width, p.bounds.height);
        r.x += b.getX();
        r.y += b.getY();

        for (Component c : hats) {
            if (c instanceof AbstrBlock && c != b) {
                PuzzleAdapter adp = findAdapter0((AbstrBlock) c, r, t);
                if (adp != null) {
                    return adp;
                }
            }
        }
        return null;
    }

    static void removeFromPuzzle(AbstrBlock b, PuzzleAdapter adp) {
        if (adp.neighbour != null) {
            if (adp.neighbour instanceof Puzzle) {
                ((Puzzle) adp.neighbour).removeFromPuzzle(b);
                adp.neighbour = null;
            }
        }
    }

    static boolean concatWithPuzzle(AbstrBlock b, PuzzleAdapter adp) {
        PuzzleAdapter a = findAdapter(b, adp, PuzzleAdapter.TYPE_DOWN);
        if (a != null) {
            removeFromPuzzle(b, adp);
            adp.neighbour = a.block;
            a.neighbour = b;
            ((Puzzle) a.block).layoutPuzzle();
            return true;
        }
        return false;
    }

    protected static void puzzleToFront(AbstrBlock blck) {
        if (!(blck instanceof Puzzle)) {
            throw new IllegalArgumentException("the block isn't a puzzle.");
        }
        blck.toFront();
        Puzzle hat = (Puzzle) blck;
        for (PuzzleAdapter p : hat.getPuzzleAdapters()) {
            if (p.neighbour instanceof Puzzle && p.type == PuzzleAdapter.TYPE_DOWN) {
                puzzleToFront(p.neighbour);
            }
        }
    }
    // <member>
    private String bspec;
    private String type;

    protected void setBlockType(String t) {
        type = t;
    }

    protected String getBlockType() {
        return type;
    }

    protected void setBlockSyntax(String s) {
        bspec = s;
    }

    protected String getBlockSyntax() {
        return bspec;
    }

    private class BlockMouseListener extends MouseAdapter {

        private boolean veto() {
            JScriptPane pne = getScriptPane();
            if (pne == null || !pne.isDragEnabled()) {
                return true;
            }
            return false;
        }

        @Override
        public void mouseDragged(MouseEvent evt) {
            if (!veto()) {
                dragEvent(evt);
            }
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            if (!veto()) {
                pressedEvent(evt);
            }
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            if (!veto()) {
                releasedEvent(evt);
            }
        }
    }
}
