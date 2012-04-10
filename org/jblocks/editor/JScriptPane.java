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
 *
 * A ScriptPane for the BlockEditor. <br />
 * 
 * @version 0.4
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

    public JScriptPane() {
        this(true);
    }

    public JScriptPane(boolean clean) {
        setBackground(Color.WHITE);
        setLayout(null);

        if (clean) {
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

    public Image getScriptPaneImage() {
        return scrp;
    }

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
     *
     * Layouts the scripts like in Scratch. <br />
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

    public void setDragEnabled(boolean b) {
        drag = b;
    }

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
     * 
     * @param c the component
     * @return the location of the component on the script pane.
     */
    public static Point getLocationOnScriptPane(JComponent c) {
        Point p = new Point();
        Container cont = c;
        while (cont != null) {
            p.x += cont.getX();
            p.y += cont.getY();
            cont = cont.getParent();
            if (cont instanceof JScriptPane) {
                break;
            }
        }
        return p;
    }
}
