package org.jblocks.stage;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author ZeroLuck
 */
public class JSwingStage extends JPanel implements Stage {

    private final List<Sprite> sprites;
    private final Dimension size;

    public JSwingStage() {
        this.sprites = new ArrayList<Sprite>();
        this.size = new Dimension(640, 480);
    }

    private void render(Graphics2D g, boolean all) {
        final Rectangle clip = new Rectangle(0, 0, size.width, size.height);
        g.setClip(g.getClipBounds().intersection(clip));

        if (all) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, size.width, size.height);

            for (Sprite s : sprites) {
                s.paint(g);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Dimension dim = getSize();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, dim.width, dim.height);

        g.translate(dim.width / 2 - size.width / 2, dim.height / 2 - size.height / 2);
        Rectangle r = g.getClipBounds();
        render((Graphics2D) g, true);
        g.setClip(r);
        g.translate(-(dim.width / 2 - size.width / 2), -(dim.height / 2 - size.height / 2));

        g.setColor(Color.BLACK);
        g.drawRect(dim.width / 2 - size.width / 2 - 1, dim.height / 2 - size.height / 2 - 1, size.width + 1, size.height + 1);
    }

    @Override
    public void repaint(Sprite s) {
        repaint();
    }

    @Override
    public void add(Sprite s) {
        synchronized (sprites) {
            sprites.add(s);
            repaint(s);
        }
    }

    @Override
    public void remove(Sprite s) {
        synchronized (sprites) {
            sprites.remove(s);
        }
    }
}
