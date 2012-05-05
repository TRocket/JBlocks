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

import org.jblocks.gui.JDragPane;

/**
 *
 * @author ZeroLuck
 */
public class JScriptVariableInput extends AbstrInput {

    private AbstrBlock variable;
    private String type;

    public JScriptVariableInput(String name, String type) {
        final BlockModel model = BlockModel.createModel("reporter", null, name);
        this.variable = BlockFactory.createBlock(model);
        this.variable.setDraggable(false);
        this.variable.setBackground(new Color(0xf3761d));
        this.type = type;

        variable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                final AbstrBlock clone = BlockFactory.createBlock(model);
                clone.setLocation(variable.getLocation());
                clone.setBackground(variable.getBackground());
                Drag.drag(JDragPane.getDragPane(JScriptVariableInput.this), JScriptVariableInput.this, evt.getPoint(), clone);
            }
        });

        super.setInput(variable);
    }

    /**
     * Returns the type of this variable. <br />
     * (Like "reporter", "cap", "command", etc...) <br />
     * The type is the value from the
     * {@link #JScriptVariableInput(java.lang.String, java.lang.String) } constructor. <br />
     * 
     * @return the type of this variable 
     */
    public String getType() {
        return type;
    }

    /**
     * WARNING: Calling this method on a JScriptVariableInput will throw an UnsupportedOperationException. <br />
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
     * WARNING: Resetting a JScriptVariableInput has no effect. <br />
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
