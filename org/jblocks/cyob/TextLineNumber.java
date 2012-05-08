/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.cyob;

import java.awt.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * This class will display line numbers for a related text text. The text
 * text must use the same line height for each line. TextLineNumber
 * supports wrapped lines and will highlight the line number of the current
 * line in the text text.
 *
 * This class was designed to be used as a text added to the row header
 * of a JScrollPane.
 * 
 * Based on: http://tips4java.wordpress.com/2009/05/23/text-component-line-number/
 * 
 * @author ZeroLuck
 */
final class TextLineNumber extends JPanel
        implements CaretListener, DocumentListener {

    // <constants>
    public final static float LEFT = 0.0f;
    public final static float CENTER = 0.5f;
    public final static float RIGHT = 1.0f;
    
    private final static Border OUTER = new MatteBorder(0, 0, 0, 2, Color.GRAY);
    private final static int L_HEIGHT = Integer.MAX_VALUE - 1000000;
    // <member>
    private JTextComponent text;
    private int borderGap;
    private Color currentLineForeground;
    private float digitAlignment;
    private int minimumDisplayDigits;
    private int lastDigits;
    private int lastHeight;
    private int lastLine;
    private HashMap<String, FontMetrics> fonts;

    /**
     *	Create a line number text for a text text. This minimum
     *  display width will be based on 3 digits.
     *
     *  @param text  the related text text
     */
    public TextLineNumber(JTextComponent component) {
        this(component, 3);
    }

    /**
     *	Create a line number text for a text text.
     *
     *  @param text  the related text text
     *  @param minimumDisplayDigits  the number of digits used to calculate
     *                               the minimum width of the text
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public TextLineNumber(JTextComponent component, int minimumDisplayDigits) {
        this.text = component;

        setFont(component.getFont());

        setBorderGap(5);
        setCurrentLineForeground(Color.DARK_GRAY);
        setDigitAlignment(RIGHT);
        setMinimumDisplayDigits(minimumDisplayDigits);

        component.getDocument().addDocumentListener(this);
        component.addCaretListener(this);
    }

    /**
     *  Gets the border gap
     *
     *  @return the border gap in pixels
     */
    public int getBorderGap() {
        return borderGap;
    }

    /**
     *  The border gap is used in calculating the left and right insets of the
     *  border. Default value is 5.
     *
     *  @param borderGap  the gap in pixels
     */
    public void setBorderGap(int borderGap) {
        this.borderGap = borderGap;
        Border inner = new EmptyBorder(0, borderGap, 0, borderGap);
        setBorder(new CompoundBorder(OUTER, inner));
        lastDigits = 0;
        setPreferredWidth();
    }

    /**
     *  Gets the current line rendering Color
     *
     *  @return the Color used to render the current line number
     */
    public Color getCurrentLineForeground() {
        return currentLineForeground == null ? getForeground() : currentLineForeground;
    }

    /**
     *  The Color used to render the current line digits.
     *
     *  @param currentLineForeground  the Color used to render the current line
     */
    public void setCurrentLineForeground(Color currentLineForeground) {
        this.currentLineForeground = currentLineForeground;
    }

    /**
     *  Gets the digit alignment
     *
     *  @return the alignment of the painted digits
     */
    public float getDigitAlignment() {
        return digitAlignment;
    }

    /**
     *  Specify the horizontal alignment of the digits within the text.
     *  Common values would be:
     *  <ul>
     *  <li>TextLineNumber.LEFT
     *  <li>TextLineNumber.CENTER
     *  <li>TextLineNumber.RIGHT (default)
     *	</ul>
     *  @param currentLineForeground  the Color used to render the current line
     */
    public void setDigitAlignment(float digitAlignment) {
        this.digitAlignment =
                digitAlignment > 1.0f ? 1.0f : digitAlignment < 0.0f ? -1.0f : digitAlignment;
    }

    /**
     *  Gets the minimum display digits
     *
     *  @return the minimum display digits
     */
    public int getMinimumDisplayDigits() {
        return minimumDisplayDigits;
    }

    /**
     *  Specify the mimimum number of digits used to calculate the preferred
     *  width of the text. Default is 3.
     *
     *  @param minimumDisplayDigits  the number digits used in the preferred
     *                               width calculation
     */
    public void setMinimumDisplayDigits(int minimumDisplayDigits) {
        this.minimumDisplayDigits = minimumDisplayDigits;
        setPreferredWidth();
    }

    /**
     *  Calculate the width needed to display the maximum line number
     */
    private void setPreferredWidth() {
        Element root = text.getDocument().getDefaultRootElement();
        int lines = root.getElementCount();
        int digits = Math.max(String.valueOf(lines).length(), minimumDisplayDigits);

        //  Update sizes when number of digits in the line number changes

        if (lastDigits != digits) {
            lastDigits = digits;
            FontMetrics fontMetrics = getFontMetrics(getFont());
            int width = fontMetrics.charWidth('0') * digits;
            Insets insets = getInsets();
            int preferredWidth = insets.left + insets.right + width;

            Dimension d = getPreferredSize();
            d.setSize(preferredWidth, L_HEIGHT);
            setPreferredSize(d);
            setSize(d);
        }
    }

    /**
     *  Draw the line numbers
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //	Determine the width of the space available to draw the line number

        FontMetrics fontMetrics = text.getFontMetrics(text.getFont());
        Insets insets = getInsets();
        int availableWidth = getSize().width - insets.left - insets.right;

        //  Determine the rows to draw within the clipped bounds.

        Rectangle clip = g.getClipBounds();
        int rowStartOffset = text.viewToModel(new Point(0, clip.y));
        int endOffset = text.viewToModel(new Point(0, clip.y + clip.height));

        while (rowStartOffset <= endOffset) {
            try {
                if (isCurrentLine(rowStartOffset)) {
                    g.setColor(getCurrentLineForeground());
                } else {
                    g.setColor(getForeground());
                }

                //  Get the line number as a string and then determine the
                //  "X" and "Y" offsets for drawing the string.

                String lineNumber = getTextLineNumber(rowStartOffset);
                int stringWidth = fontMetrics.stringWidth(lineNumber);
                int x = getOffsetX(availableWidth, stringWidth) + insets.left;
                int y = getOffsetY(rowStartOffset, fontMetrics);
                g.drawString(lineNumber, x, y);

                //  Move to the next row

                rowStartOffset = Utilities.getRowEnd(text, rowStartOffset) + 1;
            } catch (Exception e) {
            }
        }
    }

    /*
     *  We need to know if the caret is currently positioned on the line we
     *  are about to paint so the line number can be highlighted.
     */
    private boolean isCurrentLine(int rowStartOffset) {
        int caretPosition = text.getCaretPosition();
        Element root = text.getDocument().getDefaultRootElement();

        if (root.getElementIndex(rowStartOffset) == root.getElementIndex(caretPosition)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     *	Get the line number to be drawn. The empty string will be returned
     *  when a line of text has wrapped.
     */
    protected String getTextLineNumber(int rowStartOffset) {
        Element root = text.getDocument().getDefaultRootElement();
        int index = root.getElementIndex(rowStartOffset);
        Element line = root.getElement(index);

        if (line.getStartOffset() == rowStartOffset) {
            return String.valueOf(index + 1);
        } else {
            return "";
        }
    }

    /*
     *  Determine the X offset to properly align the line number when drawn
     */
    private int getOffsetX(int availableWidth, int stringWidth) {
        return (int) ((availableWidth - stringWidth) * digitAlignment);
    }

    /*
     *  Determine the Y offset for the current row
     */
    private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics)
            throws BadLocationException {
        //  Get the bounding rectangle of the row

        Rectangle r = text.modelToView(rowStartOffset);
        int lineHeight = fontMetrics.getHeight();
        int y = r.y + r.height;
        int descent = 0;

        //  The text needs to be positioned above the bottom of the bounding
        //  rectangle based on the descent of the font(s) contained on the row.

        if (r.height == lineHeight) // default font is being used
        {
            descent = fontMetrics.getDescent();
        } else // We need to check all the attributes for font changes
        {
            if (fonts == null) {
                fonts = new HashMap<String, FontMetrics>(10);
            }

            Element root = text.getDocument().getDefaultRootElement();
            int index = root.getElementIndex(rowStartOffset);
            Element line = root.getElement(index);

            for (int i = 0; i < line.getElementCount(); i++) {
                Element child = line.getElement(i);
                AttributeSet as = child.getAttributes();
                String fontFamily = (String) as.getAttribute(StyleConstants.FontFamily);
                Integer fontSize = (Integer) as.getAttribute(StyleConstants.FontSize);
                String key = fontFamily + fontSize;

                FontMetrics fm = fonts.get(key);

                if (fm == null) {
                    Font font = new Font(fontFamily, Font.PLAIN, fontSize);
                    fm = text.getFontMetrics(font);
                    fonts.put(key, fm);
                }

                descent = Math.max(descent, fm.getDescent());
            }
        }

        return y - descent;
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        //  Get the line the caret is positioned on

        int caretPosition = text.getCaretPosition();
        Element root = text.getDocument().getDefaultRootElement();
        int currentLine = root.getElementIndex(caretPosition);

        //  Need to repaint so the correct line number can be highlighted

        if (lastLine != currentLine) {
            repaint();
            lastLine = currentLine;
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        documentChanged();
    }

    /*
     *  A document change may affect the number of displayed lines of text.
     *  Therefore the lines numbers will also change.
     */
    private void documentChanged() {
        //  Preferred size of the text has not been updated at the time
        //  the DocumentEvent is fired

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                int preferredHeight = text.getPreferredSize().height;

                //  Document change has caused a change in the number of lines.
                //  Repaint to reflect the new line numbers

                if (lastHeight != preferredHeight) {
                    setPreferredWidth();
                    repaint();
                    lastHeight = preferredHeight;
                }
            }
        });
    }
}
