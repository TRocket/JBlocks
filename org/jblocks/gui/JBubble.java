package org.jblocks.gui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *
 * @author ZeroLuck
 */
public class JBubble extends JComponent {

    private final JComponent content;
    private static final int GAP = 3;
    private static final int BOTTOM = 7;

    public JBubble(JComponent c) {
        this.content = c;
        this.setOpaque(false);
        this.add(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension p = content.getPreferredSize();
        Dimension dim = new Dimension(2 * GAP + p.width, 2 * GAP + BOTTOM + p.height);
        return dim;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        content.setLocation(GAP, GAP);
        content.setSize(content.getPreferredSize());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = content.getSize();

        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, size.width + 2 * GAP, size.height + 2 * GAP, GAP * 4, GAP * 4);

        Polygon p = new Polygon();
        p.addPoint(GAP * 2 , 2 * GAP + size.height);
        p.addPoint(GAP * 2 + size.width / 2, 2 * GAP + size.height);
        p.addPoint(0, size.height + 2 * GAP + BOTTOM);
        g.fillPolygon(p);

        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    static class QueueEntry {

        final JDragPane drag;
        final JBubble bubble;

        public QueueEntry(final JDragPane drag, final JBubble bubble) {
            this.bubble = bubble;
            this.drag = drag;
        }

        public void delete() {
            drag.remove(bubble);
            drag.repaint(bubble.getBounds());
        }
    }
    
    private static List<QueueEntry> entries = new ArrayList<QueueEntry>();

    /**
     * Shows a JBubble next to a specified <code>Component c</code>. (in the {@link JDragPane}) <br/>
     * The JBubble will be closed when a {@link MouseEvent#MOUSE_PRESSED} happens. <br />
     * (The EventQueue is replaced with a new one)
     * 
     * @see org.jblocks.gui.JDragPane
     * @param c the component on which this bubble should appear.
     * @param toDisplay the component to display in the bubble
     */
    public static void showBubble(Component c, JComponent toDisplay) {
        final JDragPane drag = JDragPane.getDragPane(c);
        final JBubble bubble = new JBubble(toDisplay);
        bubble.setSize(bubble.getPreferredSize());
        bubble.setLocation(SwingUtilities.convertPoint(c, c.getWidth(), -bubble.getHeight(), drag));
        drag.add(bubble, 0);
        bubble.validate();
        bubble.repaint();
        
        entries.add(new QueueEntry(drag, bubble));

        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new EventQueue() {

            @Override
            protected void dispatchEvent(AWTEvent event) {
                if (event instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) event;
                    if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                        for (QueueEntry e : entries) {
                            e.delete();
                        }
                        entries.clear();
                    }
                }
                super.dispatchEvent(event);
            }
        });
    }
}
