package org.jblocks.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * 
 * A swing component which displays an image. <br />
 * 
 * @author TRocket
 * @author ZeroLuck
 */
public class JImagePanel extends JPanel {

    private static final long serialVersionUID = 7450533410948027737L;
    private Image image;
    private String text;

    /**
     * @throws NullPointerException if img is null.
     * @param img 
     */
    public JImagePanel(Image img) {
        if (img == null) {
            throw new NullPointerException("image is null!");
        }
        image = img;
        setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(null)));
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the

        if (text != null) {
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 18));
            g.drawString(text, image.getWidth(this) - g.getFontMetrics().stringWidth(text) - 30,
                    image.getHeight(this) - g.getFontMetrics().getHeight() - 30);
        }

    }

    /**
     * 
     * @return the text next to the image, or null if no text is defined!
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @param text the text to display next to the image
     */
    public void setText(String text) {
        this.text = text;
        repaint();
    }
}