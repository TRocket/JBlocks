package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

/**
 *
 * A hat block. <br />
 * 
 * @author ZeroLuck
 */
class JHatBlock extends AbstrBlock implements Puzzle {

    // <global>
    private static final float RND_X = 1 / 1.5F;
    private static final int RND_Y = 20;
    private static final int LEFT = 3;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 6;
    private static final int ADAPTER_W = 15;
    // <member>
    private PuzzleAdapter underMe;

    public JHatBlock() {
        underMe = new PuzzleAdapter(this, PuzzleAdapter.TYPE_DOWN);
    }

    @Override
    protected void paintBlockBorder(Graphics grp) {
        Color col = new Color(0xD6900A); //getBackground();
        Color dark = Color.BLACK;
        Color shadow = col.darker();
        Color darkShadow = shadow.darker();

        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Rectangle clip = g.getClipBounds();
        Stroke basic = g.getStroke();

        // Draw TOP
        g.setClip(new Rectangle(0, 0, size.width, RND_Y).intersection(clip));

        final int rndx = (int) (size.width * RND_X);
        final int rndy = RND_Y * 2;

        g.setPaint(new java.awt.GradientPaint(0, 0, shadow, 0, RND_Y, col));
        g.fillOval(0, 0, rndx, rndy);

        g.setColor(dark);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(0, 1, rndx, rndy - 1);
        g.drawLine(rndx + 1, RND_Y - 2, size.width - 3, RND_Y - 2);

        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setClip(clip);

        // draw LEFT and RIGHT
        g.setColor(darkShadow);
        g.drawLine(0, RND_Y, 0, size.height - BOTTOM);
        g.setColor(shadow);
        g.drawLine(1, RND_Y, 2, size.height - BOTTOM);

        g.setStroke(basic);
        g.setColor(darkShadow);
        g.drawLine(size.width - 1, RND_Y, size.width - 1, size.height - BOTTOM);
        g.setColor(shadow);
        g.drawLine(size.width - 2, RND_Y - 2, size.width - 2, size.height - BOTTOM);

        // draw BOTTOM
        g.setClip(new Rectangle(0, size.height - BOTTOM, size.width, BOTTOM).intersection(clip));

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

    @Override
    protected Insets getBorderInsets(int width, int height) {
        return new Insets(RND_Y, LEFT, BOTTOM, RIGHT);
    }

    /**
     * This method should just be called by the ScriptEditor library. <br />
     */
    @Override
    public PuzzleAdapter[] getPuzzleAdapters() {
        return new PuzzleAdapter[]{
                    underMe
                };
    }

    @Override
    public boolean contains(int x, int y) {
        Dimension size = getSize();
        if (y > RND_Y && y < size.height - BOTTOM && x > LEFT && x < size.width - RIGHT) {
            return true;
        }

        final int rndx = (int) (size.width * RND_X);
        final int rndy = RND_Y * 2;

        Ellipse2D.Float elp = new Ellipse2D.Float(0, 0, rndx, rndy);
        if (elp.contains(x, y)) {
            return true;
        }

        Rectangle rect = new Rectangle(15, size.height - BOTTOM, ADAPTER_W, BOTTOM);
        if (rect.contains(x, y)) {
            return true;
        }
        return false;
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

    @Override
    protected void pressedEvent(MouseEvent evt) {
        super.pressedEvent(evt);
        if (underMe.neighbour != null) {
            AbstrBlock.puzzleToFront(this);
        }
    }

    @Override
    protected void dragEvent(MouseEvent evt) {
        super.dragEvent(evt);
        layoutPuzzle();
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

        layoutPuzzle();
    }

    @Override
    public void removeFromPuzzle(AbstrBlock b) {
        if (underMe.neighbour == b) {
            underMe.neighbour = null;
        }
    }
}
