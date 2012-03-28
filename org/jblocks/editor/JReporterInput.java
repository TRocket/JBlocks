package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;
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

    public JReporterInput(JScriptPane p) {
        super(p);
    }

    private static Color bright(Color c, float f) {
        return new Color(Math.min((int) (c.getRed() * f), 255),
                Math.min((int) (c.getGreen() * f), 255),
                Math.min((int) (c.getBlue() * f), 255));
    }

    @Override
    protected void paintBlockBorder(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Color col = getBackground();
        Stroke basic = g.getStroke();

        g.setColor(bright(col, 1.3F));
        g.fillRoundRect(0, 0, size.width, size.height, size.height / 2, size.height / 2);

        g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
        g.setColor(col.darker());
        g.drawRoundRect(0, 0, size.width - 1, size.height - 1, size.height / 2, size.height / 2);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setStroke(basic);
    }

    @Override
    protected Insets getBorderInsets(int width, int height) {
        return new Insets(2, height / 4, 2, height / 4);
    }

    @Override
    public boolean contains(int x, int y) {
        Dimension size = getSize();

        RoundRectangle2D.Float rect = new RoundRectangle2D.Float(0, 0, size.width, size.height, size.height / 4, size.height / 4);

        return rect.contains(x, y);
    }

    @Override
    public boolean accepts(AbstrBlock block) {
        if (!isBorderEnabled()) {
            return false;
        }
        
        return block instanceof JReporterBlock;
    }

    @Override
    public Dimension getDefaultSize() {
        return new Dimension(20, 12); // shouldn't happen
    }

    @Override
    public void reset() {
        setBorderEnabled(true);
        setInput(new JReporterInput.TextInput());
    }
    
    private class TextInput extends JTextField {

        public TextInput() {
            super();
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
