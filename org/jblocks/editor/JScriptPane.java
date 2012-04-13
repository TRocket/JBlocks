package org.jblocks.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.jblocks.JBlocks;

/**
 * <p>
 * A JScriptPane is a container in which the user can drop blocks and scripts. <br />
 * It has a background-image which can be 
 * setted with {@link #setScriptPaneImage(java.awt.Image)} <br />
 * Dragging blocks from this container can be disabled with {@link #setDragEnabled(boolean) }. <br />
 * </p>
 * 
 * <p>
 * By default a JScriptPane has a JPopupMenu in which the user can cleanup the scripts. <br />
 * (You can either call {@link #cleanup() } to do this).
 * If you don't want that the user can do this you can disable this with calling 
 * the {@link org.jblocks.editor.JScriptPane#JScriptPane(boolean) } constructor.<br />
 * </p>
 * 
 * <p>
 * Dragging from this JScriptPane to another JScriptPane is possible
 * if the other JScriptPane is a (indirect) children of the same JDragPane (and dragging is enabled).
 * </p>
 * 
 * @see org.jblocks.editor.JBlockEditor
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
    private static final int CLEANUP_LEFT = 10;
    private static final int CLEANUP_SPACE = 5;
    private static final int CLEANUP_TOP = 10;
    // <member>
    private Image scrp = scriptpane;

    /**
     * Creates a new JScriptPane with an JPopupMenu. <br />
     * @see #JScriptPane(boolean) 
     */
    public JScriptPane() {
        this(true);
    }

    /**
     * Creates a new JScriptPane. <br />
     * You can select having a JPopupMenu or don't have one. <br />
     * 
     * @param popup true if you want a JPopupMenu otherwise false 
     */
    public JScriptPane(boolean popup) {
        setBackground(Color.WHITE);
        setLayout(null);

        if (popup) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem item = new JMenuItem("cleanup");
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    cleanup();
                }
            });
            menu.add(item);
            setComponentPopupMenu(menu);
        }
    }

    /**
     * Returns the background-image of this JScriptPane. <br />
     * 
     * @see #setScriptPaneImage(java.awt.Image) 
     */
    public Image getScriptPaneImage() {
        return scrp;
    }

    /**
     * Sets the background-image of this JScriptPane. <br />
     * 
     * @see #getScriptPaneImage() 
     * @param img the new background-image to display
     * @throws IllegalArgumentException if 'img' is null
     */
    public void setScriptPaneImage(Image img) {
        if (img == null) {
            throw new IllegalArgumentException("img is null!");
        }
        scrp = img;
    }

    // FIXME: replace the "-6"s.
    private static int doPuzzleH(Puzzle p, int y, Set<Component> set) {
        int h = 0;
        AbstrBlock[] up = JBlockSequence.getPuzzlePieces(p, PuzzleAdapter.TYPE_TOP);
        AbstrBlock[] down = JBlockSequence.getPuzzlePieces(p, PuzzleAdapter.TYPE_DOWN);

        up[up.length - 1].setLocation(CLEANUP_LEFT, y);
        ((Puzzle) up[up.length - 1]).layoutPuzzle();
        for (AbstrBlock b : up) {
            set.add(b);
            h += b.getHeight();
            if (b instanceof JCommandBlock || b instanceof JHatBlock) {
                h -= 6;
            }
        }

        for (AbstrBlock b : down) {
            if (!set.contains(b)) {
                set.add(b);
                h += b.getHeight();
                if (b instanceof JCommandBlock || b instanceof JHatBlock) {
                    h -= 6;
                }
            }
        }
        h += 6;


        return h;
    }

    private List<Component> getComponentsSorted() {
        List<Component> list = new LinkedList<Component>();
        list.addAll(Arrays.asList(getComponents()));
        Collections.sort(list, new Comparator<Component>() {

            @Override
            public int compare(Component t, Component t1) {
                return t.getY() - t1.getY();
            }
        });
        return list;
    }

    /**
     * Cleanups the JScriptPane so that all blocks and scripts are good visible.
     * <p />
     * (Layouts the scripts like in Scratch by MIT).
     */
    public void cleanup() {
        doLayout();
        HashSet<Component> set = new HashSet<Component>(25);

        int y = CLEANUP_TOP;

        for (Component c : getComponentsSorted()) {
            if (!set.contains(c)) {
                if (c instanceof Puzzle) {
                    y += doPuzzleH((Puzzle) c, y, set) + CLEANUP_SPACE;
                } else {
                    set.add(c);
                    Dimension size = c.getSize();
                    c.setLocation(CLEANUP_LEFT, y);
                    y += size.height + CLEANUP_SPACE;
                }
            }
        }
    }
    // <member>
    private boolean drag = true;

    /**
     * Enables/Disables dragging of blocks/scripts in this JScriptPane. <br />
     * 
     * @see #isDragEnabled() 
     * @param b true if you want drag otherwise false
     */
    public void setDragEnabled(boolean b) {
        drag = b;
    }

    /**
     * Returns true if dragging is enabled otherwise false. <br />
     * 
     * @see #setDragEnabled(boolean) 
     */
    public boolean isDragEnabled() {
        return drag;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        for (Component c : getComponents()) {
            Dimension preferred = c.getPreferredSize();
            if (!c.getSize().equals(preferred)) {
                c.setSize(preferred);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        int w = 0, h = 0;
        for (Component c : getComponents()) {
            Point p = c.getLocation();
            Dimension size = c.getPreferredSize();
            if (p.x + size.width > w) {
                w = p.x + size.width;
            }
            if (p.y + size.height > h) {
                h = p.y + size.height;
            }
        }

        return new Dimension(w + 10, h + 100);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        Image back = scrp;
        Rectangle clip = g.getClipBounds();
        int w = back.getWidth(null);
        int h = back.getHeight(null);

        for (int x = clip.x - (clip.x % w); x < (clip.x + clip.width); x += w) {
            for (int y = clip.y - (clip.y % h); y < (clip.y + clip.height); y += h) {
                g.drawImage(back, x, y, this);
            }
        }

    }

    /**
     * Returns the location of the specified JComponent of it's JScriptPane.<br />
     * 
     * @param c the component
     * @return the location of the component on it's JScriptPane.
     * @throws IllegalStateException if the component hasn't an (indirect) JScriptPane parent.
     */
    public static Point getLocationOnScriptPane(JComponent c) {
        Point p = new Point();
        Container cont = c;
        while (cont != null) {
            p.x += cont.getX();
            p.y += cont.getY();
            cont = cont.getParent();
            if (cont instanceof JScriptPane) {
                return p;
            }
        }
      //  return p;
        throw new IllegalStateException("the component has no JScriptPane parent!");
    }
}
