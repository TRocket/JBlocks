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
class JTrackPane extends JComponent {

    // <member>
    private final int height;
    private int lines;

    public JTrackPane(int h) {
        height = h;
        lines = 5;
    }
    
    public void addTrack(JSoundTrack t) {
        t.setLocation(0, 0);
        t.setSize(t.getPreferredSize());
        t.setForeground(Color.RED);
        add(t);
    }
    
    public boolean locate(final JSoundTrack track) {
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
}
