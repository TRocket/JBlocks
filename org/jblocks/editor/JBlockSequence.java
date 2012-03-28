package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;
import javax.swing.JComponent;

/**
 *
 * @author ZeroLuck
 */
class JBlockSequence extends JComponent {

    // <global>
    private static final int ADAPTER_W = 15;
    private static final int ADAPTER_X = 15;
    private static final int ADAPTER_H = 6;
    // <member>
    private JScriptPane pane;
    private AbstrBlock stack;

    public JBlockSequence(JScriptPane p) {
        if (p == null) {
            throw new IllegalArgumentException("p is null!");
        }
        pane = p;
    }

    /**
     *  Returns the bounds of the adapter with the type t of the puzzle.
     *  (or null)
     */
    private static PuzzleAdapter getAdapterFor(Puzzle p, int t) {
        PuzzleAdapter[] adps = p.getPuzzleAdapters();
        for (PuzzleAdapter a : adps) {
            if (a.type == t) {
                return a;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        AbstrBlock[] comp = getPuzzlePieces((Puzzle) stack, PuzzleAdapter.TYPE_DOWN);
        Dimension dim;
        if (comp.length > 0) {
            int xmax = 0;
            int ymax = 0;
            int lastp = 0;
            for (Component c : comp) {
                Dimension p = c.getPreferredSize();
                c.setLocation(0, ymax);
                c.setSize(p);
                if (p.width > xmax) {
                    xmax = p.width;
                }
                ymax += p.height;
                if (c instanceof Puzzle) {
                    PuzzleAdapter pa = getAdapterFor((Puzzle) c, PuzzleAdapter.TYPE_DOWN);
                    if (pa != null) {
                        lastp = pa.bounds.height;
                        ymax -= pa.bounds.height;
                    } else {
                        lastp = 0;
                    }
                }
            }
            ymax += 1 + lastp;
            dim = new Dimension(Math.max(75, xmax + 10), ymax);
        } else {
            dim = new Dimension(75, 35);
        }
        setPreferredSize(dim);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        if (!isValid()) {
            validate();
        }
        return super.getPreferredSize();
    }

    /**
     * 
     * @return the block sequence's stack.
     * @see #setStack(AbstrBlock)
     */
    public AbstrBlock getStack() {
        return stack;
    }

    /**
     * 
     * @see #getStack() 
     * @param hat the new hat of the stack
     */
    public void setStack(AbstrBlock hat) {
        if (hat == null) {
            stack = null;
            removeAll();
            return;
        }
        if (hat instanceof Puzzle) {
            stack = hat;
            removeAll();
            AbstrBlock[] seq = getPuzzlePieces((Puzzle) hat, PuzzleAdapter.TYPE_DOWN);
            for (AbstrBlock b : seq) {
                add(b);
            }
        } else {
            throw new IllegalArgumentException("hat is not a instanceof Puzzle.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Color col = getBackground();
        Color shadow = col.darker();
        Color gray = Color.LIGHT_GRAY;
        Stroke basic = g.getStroke();
        Shape clip = g.getClip();

        // BACKGROUND
        g.setColor(gray);
        g.fillRect(0, 0, size.width, size.height);

        // TOP
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));

        g.setColor(col);
        g.fillRoundRect(ADAPTER_X, 0 - 5, ADAPTER_W, ADAPTER_H + 5, 5, 5);
        g.setColor(shadow);
        g.drawRoundRect(ADAPTER_X, 0 - 5, ADAPTER_W, ADAPTER_H + 5, 5, 5);
        g.setColor(col);
        g.fillRect(0, 0, size.width, 0 - 2);
        g.setColor(shadow);
        g.drawLine(0, 0, ADAPTER_X, 0);
        g.drawLine(ADAPTER_X + ADAPTER_W, 0, size.width, 0);

        // LEFT + RIGHT

        g.setColor(col);
        g.fillRect(0, 0, 0, size.height);
        g.fillRect(size.width, 0, 0, size.height);
        g.setColor(shadow);
        g.drawLine(0, 0, 0, size.height - ADAPTER_H);
        g.drawLine(size.width - 1, 0, size.width - 1, size.height - ADAPTER_H);

        // BOTTOM
        g.setColor(col);
        g.fillRect(0, size.height - ADAPTER_H, ADAPTER_X + 2, ADAPTER_H);
        g.fillRect(ADAPTER_X + ADAPTER_W, size.height - ADAPTER_H, size.width, ADAPTER_H);
        g.setColor(shadow);
        g.drawLine(0, size.height - ADAPTER_H, ADAPTER_X, size.height - ADAPTER_H);
        g.drawLine(ADAPTER_X + ADAPTER_W, size.height - ADAPTER_H, size.width, size.height - ADAPTER_H);

        g.setClip(ADAPTER_X, size.height - ADAPTER_H, ADAPTER_W, ADAPTER_H);
        g.drawRoundRect(ADAPTER_X, size.height - ADAPTER_H - 5, ADAPTER_W - 1, ADAPTER_H + 4, 5, 5);

        g.setClip(clip);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setStroke(basic);
    }

    /**
     * 
     * finds a possible sequence for the block b. <br />
     * 
     * @param cont - cont = b
     * @param b - the block
     * @param r - the bounds of the block b.
     */
    static JBlockSequence findSequence(JComponent cont, Rectangle r, AbstrBlock b) {
        for (Component comp : cont.getComponents()) {
            if (comp == b) {
                continue;
            }
            if (comp instanceof JBlockSequence) {
                JBlockSequence inp = (JBlockSequence) comp;
                if (inp.getStack() != null) {
                    JBlockSequence inp2 = findSequence((JComponent) comp, r, b);
                    if (inp2 != null) {
                        return inp2;
                    }
                    continue;
                }
                Point p = JScriptPane.getLocationOnScriptPane(inp);
                Rectangle rect = new Rectangle(p, inp.getSize());
                if (rect.intersects(r)) {
                    return inp;
                }
            }
            if (comp instanceof JComponent) {
                JBlockSequence inp = findSequence((JComponent) comp, r, b);
                if (inp != null) {
                    return inp;
                }
            }
        }
        return null;
    }

    /**
     * removes a block-puzzle from a sequence. <br />
     * 
     * @throws ClassCastException, NullPointerException
     * @param b - the hat of the block-puzzle 
     */
    static void removeFromSequence(AbstrBlock b) {
        JScriptPane pane = b.getScriptPane();
        JBlockSequence seq = (JBlockSequence) b.getParent();
        if (seq.getStack() == b) {
            seq.setStack(null);
        } else {
            AbstrBlock[] blcks = getPuzzlePieces((Puzzle) b, PuzzleAdapter.TYPE_TOP);
            if (blcks.length > 0) {
                seq.setStack(blcks[1]);
            } else {
                seq.setStack(null);
            }
            AbstrBlock.removeFromPuzzle(b, getAdapterFor((Puzzle) b, PuzzleAdapter.TYPE_TOP));
        }

        AbstrBlock[] blcks = getPuzzlePieces((Puzzle) b, PuzzleAdapter.TYPE_DOWN);
        for (AbstrBlock item : blcks) {
            Point p = JScriptPane.getLocationOnScriptPane(b);
            b.setLocation(p);
            pane.add(item);
        }


        b.layoutRoot();
    }

    /**
     * 
     * @param b - the block.
     * @return true if a sequence was found.
     */
    static boolean concatWithSequence(AbstrBlock b) {
        JScriptPane pane = b.getScriptPane();
        JBlockSequence seq = findSequence(pane,
                new Rectangle(JScriptPane.getLocationOnScriptPane(b), b.getSize()), b);
        if (seq != null) {
            b.getParent().remove(b);
            seq.setStack(b);
            pane.validate();
            return true;
        }
        return false;
    }

    static AbstrBlock[] getPuzzlePieces(Puzzle p, int t) {
        if (p == null) {
            return new AbstrBlock[0];
        }
        ArrayList<AbstrBlock> list = new ArrayList<AbstrBlock>();
        PuzzleAdapter[] adps = p.getPuzzleAdapters();
        while (adps != null && adps.length > 0) {
            list.add(adps[0].block);
            boolean brk = false;
            for (PuzzleAdapter a : adps) {
                if (a.type == t) {
                    if (a.neighbour instanceof Puzzle) {
                        adps = ((Puzzle) a.neighbour).getPuzzleAdapters();
                        brk = true;
                        break;
                    }
                    adps = null;
                    brk = true;
                    break;
                }
            }
            if (!brk) {
                adps = null;
            }
        }
        return list.toArray(new AbstrBlock[0]);
    }
}
