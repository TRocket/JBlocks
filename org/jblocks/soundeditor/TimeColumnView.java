package org.jblocks.soundeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 *
 * @author ZeroLuck
 */
class TimeColumnView extends JComponent {

    private int dist;

    public TimeColumnView(int distance) {
        dist = distance;
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(Short.MAX_VALUE, 5 + fm.getHeight() + 2);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        Dimension size = getSize();
        Rectangle clip = g.getClipBounds();
        if (clip == null) {
            clip.x = 0;
            clip.y = 0;
            clip.width = getWidth();
            clip.height = getHeight();
        }
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        g.drawLine(0, 0, 0, size.height);
        for (int i = Math.max(dist, clip.x - clip.x % dist); i < clip.x + clip.width; i += dist) {
            int num = (i / dist);
            int minutes = (num / 60);
            num %= 60;
            String str = "" + num;
            if (str.length() == 1) {
                str = "0" + str;
            }
            str = minutes + ":" + str;
            g.drawString(str, i - fm.stringWidth(str) / 2, fm.getHeight());
            g.drawLine(i, getHeight() - 5, i, size.height);
        }
    }
}
