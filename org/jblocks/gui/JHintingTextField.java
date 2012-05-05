package org.jblocks.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

/**
 * An enhanced text field, showing an {@link #getEmptyTextHint() empty text}
 * hint on its view, if the {@link #getText() text content} is empty.
 * 
 * @author Sebastian Haufe, ZeroLuck
 */
public class JHintingTextField extends JTextField {

    /**
     * Creates a new <code>JHintingTextField</code>.
     */
    public JHintingTextField() {
        installHighlightPainter();
    }

    /**
     * Creates a new <code>JHintingTextField</code>.
     * 
     * @param columns the number of preferred columns to calculate preferred
     *          width
     */
    public JHintingTextField(int columns) {
        super(columns);
        installHighlightPainter();
    }

    /**
     * Creates a new <code>JHintingTextField</code>.
     * 
     * @param text the text to show in the text field
     */
    public JHintingTextField(String text) {
        super(text);
        installHighlightPainter();
    }

    /**
     * Creates a new <code>JHintingTextField</code>.
     * 
     * @param text the text to show in the text field
     * @param columns the number of preferred columns to calculate preferred
     *          width
     */
    public JHintingTextField(String text, int columns) {
        super(text, columns);
        installHighlightPainter();
    }

    /**
     * Creates a new <code>JHintingTextField</code>.
     * 
     * @param doc the text model
     * @param text the text to show in the text field
     * @param columns the number of preferred columns to calculate preferred
     *          width
     */
    public JHintingTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        installHighlightPainter();
    }
    
    private void installHighlightPainter() {
        Highlighter highlighter = getHighlighter();
        try {
            highlighter.addHighlight(0, 0, createHighlightPainter());
        } catch (BadLocationException ex) {
        }
    }

    protected HighlightPainter createHighlightPainter() {
        return new Highlighter.HighlightPainter() {

            final JLabel label = new JLabel("",
                    SwingConstants.TRAILING);
            final int gap = 3;

            @Override
            public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
                final String hint = (String) c.getClientProperty("emptyTextHint");
                if (hint == null
                        || hint.length() == 0
                        || c.getDocument().getLength() != 0) {
                    return;
                }
                label.setText(hint);

                final Insets ins = c.getInsets();
                final boolean ltr = c.getComponentOrientation().isLeftToRight();
                if (ltr) {
                    ins.right += gap;
                } else {
                    ins.left += gap;
                }

                final Dimension pref = label.getPreferredSize();
                final int prHeight = pref.height;
                final int prWidth = pref.width;
                final int w = Math.min(c.getWidth() - ins.left - ins.right, prWidth);
                final int h = Math.min(c.getWidth() - ins.top - ins.bottom, prHeight);
                final int x = !ltr ? c.getWidth() - ins.right - w : ins.left;
                final int parentHeight = c.getHeight() - ins.top - ins.bottom;
                final int y = ins.top + (parentHeight - h) / 2;
                label.setForeground(Color.GRAY);
                label.setOpaque(false);
                SwingUtilities.paintComponent(g, label, c, x, y, w, h);
            }
        };
    }

    /**
     * Returns the emptyTextHint.
     * 
     * @return the emptyTextHint
     */
    public String getEmptyTextHint() {
        return (String) getClientProperty("emptyTextHint");
    }

    /**
     * Sets the emptyTextHint.
     * 
     * @param hint the emptyTextHint to set
     */
    public void setEmptyTextHint(String hint) {
        putClientProperty("emptyTextHint", hint);
        repaint();
    }
}
