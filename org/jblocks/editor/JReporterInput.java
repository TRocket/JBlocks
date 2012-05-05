package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * Input class for reporter-blocks. <br />
 * 
 * @author ZeroLuck
 */
class JReporterInput extends AbstrInput {

    /**
     * The input will accept reporter-blocks and text input. <br />
     */
    public static final int TYPE_TEXT_AND_REPORTER = 0;
    /**
     * The input will just accept reporter-blocks. <br />
     */
    public static final int TYPE_REPORTER = 1;
    //
    //
    private int type;

    public JReporterInput() {
        super();
        type = TYPE_TEXT_AND_REPORTER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintBlockBorder(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Color col = getBackground();
        Stroke basic = g.getStroke();

        g.setColor(Colors.bright(col, 1.2F));
        g.fillRoundRect(0, 0, size.width, size.height, size.height / 2, size.height / 2);

        g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
        g.setColor(col.darker());
        g.drawRoundRect(0, 0, size.width - 1, size.height - 1, size.height / 2, size.height / 2);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setStroke(basic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Insets getBorderInsets(int width, int height) {
        return new Insets(2, height / 4, 2, height / 4);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(int x, int y) {
        Dimension size = getSize();

        RoundRectangle2D.Float rect = new RoundRectangle2D.Float(0, 0, size.width, size.height, size.height / 4, size.height / 4);

        return rect.contains(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(AbstrBlock block) {
        JComponent inp = getInput();
        if (inp instanceof JReporterBlock) {
            return false;
        }
        return block instanceof JReporterBlock;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getDefaultSize() {
        return new Dimension(20, 12); // shouldn't happen
    }

    /**
     * Sets the type of this JReporterInput. <br />
     * <code>reset()</code> has to be called. <br />
     * 
     * @see #reset() 
     * @see #TYPE_REPORTER
     * @see #TYPE_TEXT_AND_REPORTER
     * @see #TYPE_NUMBERS_AND_REPORTER
     * @param t - the new type of this JReporterInput.
     */
    public void setType(int t) {
        type = t;
    }

    /**
     * Returns the type of this JReporterInput. <br />
     * @see #setType(int) 
     */
    public int getType() {
        return type;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Component inp = getInput();
        JScriptPane pane = getScriptPane();
        if (inp instanceof JTextField) {
            JTextField txt = (JTextField) inp;
            txt.setEditable(!(pane == null || !pane.isDragEnabled()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        if (type == TYPE_REPORTER) {
            setBorderEnabled(true);
            setInput(null);
        } else if (type == TYPE_TEXT_AND_REPORTER) {
            setBorderEnabled(false);
            TextInput txt = new TextInput();
            setInput(txt);
        }
    }

    private class TextInput extends JTextField {

        public TextInput() {
            this.getDocument().addDocumentListener(new DocumentListener() {

                private void update() {
                    JReporterInput.this.layoutRoot();
                }

                @Override
                public void insertUpdate(DocumentEvent de) {
                    update();
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    update();
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    update();
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension dim = super.getPreferredSize();
            FontMetrics fm = super.getFontMetrics(getFont());
            return new Dimension(Math.max(26, dim.width + fm.charWidth(' ') * 4), dim.height);
        }
    }
}
