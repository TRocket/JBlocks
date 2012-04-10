package org.jblocks.byob;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;

/**
 *
 * @author ZeroLuck
 */
class Plus extends JComponent {

    // <global>
    private static final int PLUS_WIDTH = 13;
    private static final int PLUS_HEIGHT = 13;
    private static final int CROSS_GAP = 4;
    // <member>
    private boolean highlight;

    public Plus() {
        setOpaque(false);
        addMouseListener(new PlusMouseListener());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PLUS_WIDTH + 1, PLUS_HEIGHT + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0xff5000 + (highlight ? 0x5000 : 0)));
        g.fillOval(0, 0, PLUS_WIDTH, PLUS_HEIGHT);

        g.setColor(Color.BLACK);
        g.drawOval(0, 0, PLUS_WIDTH, PLUS_HEIGHT);

        g.drawLine(CROSS_GAP, PLUS_HEIGHT / 2, PLUS_WIDTH - CROSS_GAP, PLUS_HEIGHT / 2);
        g.drawLine(PLUS_WIDTH / 2, CROSS_GAP, PLUS_WIDTH / 2, PLUS_HEIGHT - CROSS_GAP);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    private void fireActionEvent() {
        for (Object o : super.listenerList.getListenerList()) {
            if (o instanceof ActionListener) {
                ActionListener a = (ActionListener) o;
                a.actionPerformed(new java.awt.event.ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }
        }
    }

    private class PlusMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent me) {
            fireActionEvent();
        }

        @Override
        public void mousePressed(MouseEvent me) {
        }

        @Override
        public void mouseReleased(MouseEvent me) {
        }

        @Override
        public void mouseEntered(MouseEvent me) {
            highlight = true;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent me) {
            highlight = false;
            setCursor(Cursor.getDefaultCursor());
            repaint();
        }
    }
}
