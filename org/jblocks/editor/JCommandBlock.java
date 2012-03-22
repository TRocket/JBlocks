package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

/**
 *
 * @author ZeroLuck
 */
public class JCommandBlock extends AbstrBlock implements Puzzle {

    // <global>
    private static final int TOP = 6;
    private static final int LEFT_RIGHT = 2;
    private static final int BOTTOM = TOP;
    private static final int ADAPTER_W = 15;
    // <member>
    private PuzzleAdapter overMe;
    private PuzzleAdapter underMe;

    public JCommandBlock(JScriptPane pane) {
        super(pane);
        underMe = new PuzzleAdapter(this, PuzzleAdapter.TYPE_DOWN);
        overMe = new PuzzleAdapter(this, PuzzleAdapter.TYPE_TOP);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void layoutPuzzle() {
        if (underMe.neighbour != null) {
            underMe.neighbour.setLocation(getX(), getY() + getHeight() - BOTTOM);
            if (underMe.neighbour instanceof Puzzle) {
                ((Puzzle) underMe.neighbour).layoutPuzzle();
            }
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        super.doLayout();
        Dimension size = getSize();
        underMe.bounds.x = 15;
        underMe.bounds.y = size.height - BOTTOM;
        underMe.bounds.width = ADAPTER_W;
        underMe.bounds.height = BOTTOM;
        overMe.bounds.x = 15;
        overMe.bounds.y = 0;
        overMe.bounds.width = ADAPTER_W;
        overMe.bounds.height = TOP;

        layoutPuzzle();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void paintBlockBorder(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        Color col = getBackground();
        Rectangle clip = g.getClipBounds();
        Dimension size = getSize();
        Stroke basic = g.getStroke();

        Color shadow = col.darker();
        Color darkShadow = shadow.darker();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new java.awt.BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));

        // TOP
        g.setColor(col);
        g.fillRect(0, 0, 15, TOP);
        g.fillRect(15 + ADAPTER_W, 0, size.width, TOP);
        g.setColor(shadow);
        g.drawRoundRect(15, -10, 15, 10 + TOP, 5, 5);
        g.drawLine(0, 0, 15, 0);
        g.drawLine(15 + ADAPTER_W, 0, size.width - 2, 0);

        // draw LEFT and RIGHT
        g.setColor(shadow);
        g.drawLine(0, 0, 0, size.height - BOTTOM);


        g.setStroke(basic);
        g.setColor(darkShadow);
        g.drawLine(size.width - 1, 0, size.width - 1, size.height - BOTTOM);
        g.setColor(shadow);
        g.drawLine(size.width - 2, 2, size.width - 2, size.height - BOTTOM);


        // draw BOTTOM
        g.setClip(0, size.height - BOTTOM, size.width, BOTTOM);

        g.setColor(shadow);
        g.drawLine(0, size.height - BOTTOM, size.width, size.height - BOTTOM);
        g.setColor(darkShadow);
        g.drawLine(1, size.height - BOTTOM + 1, size.width - 1, size.height - BOTTOM + 1);

        g.setColor(col);
        g.fillRoundRect(15, size.height - BOTTOM - 5, ADAPTER_W, BOTTOM + 5, 5, 5);
        g.setColor(darkShadow);
        g.drawRoundRect(15, size.height - BOTTOM - 5, ADAPTER_W, BOTTOM + 4, 5, 5);

        g.setClip(clip);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Insets getBorderInsets(int width, int height) {
        return new Insets(TOP, LEFT_RIGHT, BOTTOM, LEFT_RIGHT);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean contains(int x, int y) {
        Dimension size = getSize();
        Polygon pol = new Polygon();
        pol.addPoint(0, 0);
        pol.addPoint(15, 0);
        pol.addPoint(15, TOP);
        pol.addPoint(15 + ADAPTER_W, TOP);
        pol.addPoint(15 + ADAPTER_W, 0);
        pol.addPoint(size.width - 1, 0);
        pol.addPoint(size.width - 1, size.height - BOTTOM);
        pol.addPoint(15 + ADAPTER_W, size.height - BOTTOM);
        pol.addPoint(15 + ADAPTER_W, size.height - 1);
        pol.addPoint(15, size.height - 1);
        pol.addPoint(15, size.height - BOTTOM);
        pol.addPoint(0, size.height - BOTTOM);
        return pol.contains(x, y);
    }

    /**
     * 
     * This method should just be called by the ScriptEditor library. <br />
     */
    @Override
    public PuzzleAdapter[] getPuzzleAdapters() {
        return new PuzzleAdapter[]{
                    overMe,
                    underMe
                };
    }

    private PuzzleAdapter findAdapter0(AbstrBlock b, Rectangle r) {
        if (b instanceof Puzzle) {
            Puzzle p = (Puzzle) b;
            PuzzleAdapter[] adapters = p.getPuzzleAdapters();
            for (PuzzleAdapter a : adapters) {
                Rectangle clone = new Rectangle(a.bounds.x, a.bounds.y,
                        a.bounds.width, a.bounds.height);
                clone.add(b.getLocation());
                if (clone.intersects(r) && a.neighbour == null
                        && a.type == PuzzleAdapter.TYPE_DOWN) {
                    return a;
                }
            }
        }
        return null;
    }

    private void findAdapter() {
        if (overMe.neighbour != null) {
            // FIXME: Unknown bug, this doesn't work yet :/
            if (overMe.neighbour instanceof Puzzle) {
                ((Puzzle) overMe.neighbour).removeFromPuzzle(this);

                overMe.neighbour = null;
            } else {
                return;
            }
        }
        Component[] hats = pane.getComponents();
        Rectangle r = new Rectangle(overMe.bounds.x, overMe.bounds.y,
                overMe.bounds.width, overMe.bounds.height);
        r.add(getLocation());

        for (Component c : hats) {
            if (c instanceof AbstrBlock && c != this) {
                PuzzleAdapter p = findAdapter0((AbstrBlock) c, r);
                if (p != null) {
                    overMe.neighbour = (AbstrBlock) c;
                    p.neighbour = this;
                    ((Puzzle) p.block).layoutPuzzle();
                    return;
                }
            }
        }
    }

    @Override
    public void pressedEvent(MouseEvent evt) {
        if (overMe.neighbour != null) {
            if (overMe.neighbour instanceof Puzzle) {
                ((Puzzle) overMe.neighbour).removeFromPuzzle(this);
                overMe.neighbour = null;
            } else {
                return;
            }
        }
        super.pressedEvent(evt);
    }

    @Override
    public void dragEvent(MouseEvent evt) {
        if (overMe.neighbour != null) {
            return;
        }
        super.dragEvent(evt);
        layoutPuzzle();
    }

    @Override
    protected void releasedEvent(MouseEvent evt) {
        super.releasedEvent(evt);
        findAdapter();
    }

    @Override
    public void removeFromPuzzle(AbstrBlock b) {
        if (underMe.neighbour == b) {
            underMe.neighbour = null;
        }
    }
}