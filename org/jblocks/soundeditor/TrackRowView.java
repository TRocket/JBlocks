package org.jblocks.soundeditor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 *
 * @author ZeroLuck
 */
class TrackRowView extends JComponent {

    private int dist;
    private int max;

    public TrackRowView(int distance, int max) {
        this.dist = distance;
        this.max = max;
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(5 + fm.stringWidth("" + max) + 20, max * dist);
    }

    @Override
    public void paintComponent(Graphics g) {
        Dimension size = getSize();
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        g.drawLine(size.width - 1, 0, size.width - 1, size.height);
        for (int i = 0; i < max; i++) {
            g.drawLine(0, i * dist, size.width, i * dist);
            String str = "" + (i + 1);
            g.drawString(str, 2, i * dist + fm.getHeight());
        }

        g.drawLine(0, max * dist, size.width, max * dist);
    }
}
