package org.jblocks.soundeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 *
 * @author ZeroLuck
 */
public final class JTrackPane extends JComponent {

    // <member>
    private final int height;
    private int lines;
    private final CurrentPos pos;

    public JTrackPane(int h, int max) {
        height = h;
        lines = max;
        pos = new CurrentPos(h * max);
        add(pos, 0);
        resetPos();
    }

    void addTrack(JSoundTrack t) {
        t.setForeground(Color.RED);
        if (getComponentCount() > 0) {
            add(t, 1);
        } else {
            add(t);
        }
        doLayout();
    }

    void addTrackLocated(JSoundTrack t) {
        t.setLocation(0, 0);
        if (getComponentCount() > 0) {
            add(t, 1);
        } else {
            add(t);
        }
        doLayout();
    }

    @Override
    public void doLayout() {
        for (Component c : getComponents()) {
            c.setSize(c.getPreferredSize());
        }
    }

    void removeTracks() {
        for (Component c : getComponents()) {
            if (c instanceof JSoundTrack) {
                remove(c);
            }
        }
        validate();
        repaint();
    }

    void resetPos() {
        setPos(-100);
    }

    void setPos(int p) {
        pos.setPos(p);
        repaint();
    }

    int getPos() {
        return pos.getPos();
    }

    boolean locate(final JSoundTrack track) {
        int x = track.getX();
        int y = track.getY();
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (y % height > height / 2) {
            y += height;
        }
        if (y >= height * lines) {
            return false;
        }
        track.setLocation(x, y - y % height);
        for (final Component c : getComponents()) {
            if (c != track) {
                Rectangle r = c.getBounds();
                if (r.intersects(track.getBounds())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(20000, lines * height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        Dimension size = getSize();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size.width, lines * height);
        g.setColor(Color.BLACK);
        for (int i = 0; i < lines + 1; i++) {
            g.drawLine(0, i * height, size.width, i * height);
        }
    }

    void toFront(Component c) {
        if (c.getParent() != this) {
            return;
        }
        remove(c);
        if (getComponentCount() > 0) {
            add(c, 1);
        } else {
            add(c);
        }
        repaint();
    }

    private static class CurrentPos extends JComponent {

        private static final int LINE_WIDTH = 3;
        private final int height;

        public CurrentPos(int h) {
            height = h;
        }

        public void setPos(int pos) {
            setLocation(pos - LINE_WIDTH / 2, 0);
        }

        public int getPos() {
            return getX() + LINE_WIDTH / 2;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(LINE_WIDTH, height);
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, LINE_WIDTH, height);
        }
    }
}
