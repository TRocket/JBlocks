package org.jblocks.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.jblocks.gui.JDragPane;

/**
 *
 * @author ZeroLuck
 */
public class JVariableInput extends AbstrInput {

    private JReporterBlock variable;
    private JLabel variableText;

    public JVariableInput(String name) {
        variable = new JReporterBlock();
        variableText = new JLabel(name);
        variable.add(variableText);
        variable.setDraggable(false);
        variable.setBackground(new Color(0xf3761d));

        variable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                AbstrBlock clone = new JReporterBlock();
                clone.add(new JLabel(getText()));
                clone.setLocation(variable.getLocation());
                clone.setBackground(variable.getBackground());
                Drag.drag(JDragPane.getDragPane(JVariableInput.this), JVariableInput.this, evt.getPoint(), clone);
            }
        });

        super.setInput(variable);
    }

    /**
     * Sets the text (name) of the input's variable. <br />
     * 
     * @param txt the text to display
     */
    public void setText(String txt) {
        variableText.setText(txt);
    }

    /**
     * Returns the text (name) of this input's variable. <br />
     */
    public String getText() {
        return variableText.getText();
    }

    /**
     * WARNING: Calling this method on a JVariableInput will throw an UnsupportedOperationException. <br />
     * <hr />
     * {@inheritDoc}
     */
    @Override
    public void setInput(JComponent c) {
        throw new java.lang.UnsupportedOperationException("this operation isn't allowed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(int x, int y) {
        return getInput().contains(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(AbstrBlock block) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Insets getBorderInsets(int width, int height) {
        return new Insets(2, 2, 2, 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintBlockBorder(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension size = getSize();
        Shape clip = g.getClip();
        Stroke s = g.getStroke();
        g.setStroke(new BasicStroke(3));
        int rxy = variable.getHeight() / 2;
        g.setColor(new Color(0x909090));
        g.drawRoundRect(1, 1, size.width - 2, size.height - 2, rxy, rxy);
        g.setColor(new Color(0x303030));
        g.setClip(0, size.height / 2, size.width, size.height / 2);
        g.drawRoundRect(1, 1, size.width - 2, size.height - 2, rxy, rxy);
        g.setStroke(s);
        g.setClip(clip);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    /**
     * WARNING: Resetting a JVariableInput has no effect. <br />
     * <hr />
     * {@inheritDoc}
     */
    @Override
    public void reset() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getDefaultSize() {
        return new Dimension();
    }
}
