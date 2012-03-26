package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import javax.swing.JComponent;

/**
 *
 * @author ZeroLuck
 */
public class JBlockSequence extends JComponent {

    // <global>
    private static final int ADAPTER_A_Y = 0;
    private static final int ADAPTER_W = 15;
    private static final int ADAPTER_X = 15;
    private static final int ADAPTER_H = 6;
    // <member>
    private JScriptPane pane;

    public JBlockSequence(JScriptPane p) {
        if (p == null) {
            throw new IllegalArgumentException("p is null!");
        }
        pane = p;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(150, 75);
    }

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
}
