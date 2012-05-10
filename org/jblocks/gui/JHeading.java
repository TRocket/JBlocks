package org.jblocks.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * A JHeading is like a decorated JLabel. <br />
 * 
 * @author ZeroLuck
 */
public class JHeading extends JPanel {

    private final JLabel label;

    /**
     * Creates a new <code>JHeading</code> with the specified text. <br />
     * 
     * @see #setText(java.lang.String) 
     * @param text the heading's text.
     */
    public JHeading(String text) {
        super(new FlowLayout(FlowLayout.LEFT));
        label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16));
        label.setForeground(new Color(0xff2c4885));
        add(label);
    }

    /**
     * Sets the text of this heading. <br />
     */
    public void setText(String text) {
        label.setText(text);
    }

    /**
     * Returns the text of this heading. <br />
     */
    public String getText() {
        return label.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (UIManager.getLookAndFeel().getName().indexOf("Nimbus") != -1) {
            Graphics2D g2D = (Graphics2D) g;
            g2D.setPaint(new GradientPaint(0, 0, new Color(0xb5cdde), 0, getHeight(), new Color(0x9cb8ce)));
            //     g2D.setPaint(new GradientPaint(0, 0, new Color(0xfff9fafb), 0, getHeight(), new Color(0xffd6d9df)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(0xffdef2fe));
            g.drawLine(0, 0, getWidth(), 0);
            g.setColor(new Color(0xff8c9ea7));
            g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        }
    }
}
