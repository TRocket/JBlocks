package org.jblocks.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.jblocks.JBlocks;

public class Splash extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -6966282063729475150L;
    private static BufferedImage splashImage;
    
    static {
        try {
            splashImage = ImageIO.read(JBlocks.class.getResourceAsStream("res/splash.png"));
        } catch (IOException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    private JImagePanel image;
    private JPanel panel = new JPanel();
    private JProgressBar progbar;
    
    public Splash() {
        progbar = new JProgressBar();
        image = new JImagePanel(splashImage);
        panel.add(image);
        this.add(image);

        //panel.add(progbar);
        this.setUndecorated(true);
        this.setSize(640, 400);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocation(x, y);
        BufferedImage icon = null;
        
        this.setIconImage(icon);
        
        image.paint(getGraphics());
    }
    
    public String getText() {
        return image.getText();
    }
    
    public void setText(String text) {
        image.setText(text);
        this.repaint();
    }
}
