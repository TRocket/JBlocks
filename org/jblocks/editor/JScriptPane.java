package org.jblocks.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.jblocks.JBlocks;

/**
 *
 * A ScriptPane for the BlockEditor. <br />
 * 
 * @version 0.1
 * @author ZeroLuck
 */
public class JScriptPane extends JPanel {
    
    private static BufferedImage scriptpane;
    
    static {
        try {
            scriptpane = ImageIO.read(JBlocks.class.getResourceAsStream("res/scriptpane.png"));
        } catch (IOException ex) {
            throw new java.lang.ExceptionInInitializerError(ex);
        }
    }
    
    public JScriptPane() {
        setBackground(Color.WHITE);
        setLayout(null);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        BufferedImage back = scriptpane;
        Rectangle clip = g.getClipBounds();
        int w = back.getWidth();
        int h = back.getHeight();
        
        for (int x = clip.x - (clip.x % w); x < (clip.x + clip.width); x += w) {
            for (int y = clip.y - (clip.y % h); y < (clip.y + clip.height); y += h) {
                g.drawImage(back, x, y, this);
            }
        }
    }
    
}
