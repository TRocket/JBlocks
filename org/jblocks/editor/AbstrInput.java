package org.jblocks.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.JComponent;

/**
 *
 * An abstract class for block-inputs. <br />
 * A block-input can be a text-field or a place where you can insert <br />
 * an other block. <br />
 * <br />
 * 
 * Implementations of this class have to draw the input's border <br />
 * and some input specified methods. <br />
 * 
 * @see AbstrBlock
 * @author ZeroLuck
 */
public abstract class AbstrInput extends JComponent {

    private JComponent comp;
    private JScriptPane pane;

    /**
     * @throws NullPointerException if p is null.
     * @param p the script pane for this block-input.
     */
    public AbstrInput(JScriptPane p) {
        if (pane == null) {
            throw new NullPointerException();
        }
        pane = p;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public abstract boolean contains(int x, int y);

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }
    
    /**
     * 
     * @return returns true if the input-field accepts this block as an input.
     */
    public abstract boolean accepts(AbstrBlock block);

    /**
     * Returns the insets of the border. <br />
     * 
     * @param size the size of the component
     * @see #paintBorder(java.awt.Graphics) 
     */
    public abstract Insets getBorderInsets(int width, int height);

    /**
     * Draws the input's border. <br />
     * 
     * @param g the graphics object on which to draw.
     * @see #getBorderInsets(int, int) 
     */
    @Override
    public abstract void paintBorder(Graphics g);

    /**
     * @return returns the size which is used when this input-field has no input.
     */
    public abstract Dimension getDefaultSize();

    /**
     * 
     * Sets the default input-field's input component. <br />
     */
    public abstract JComponent reset();

    @Override
    public Dimension getPreferredSize() {
        if (!isValid()) {
            doLayout();
        }
        return super.getPreferredSize();
    }

    @Override
    public void doLayout() {
        Dimension dim;
        if (comp == null) {
            dim = getDefaultSize();
        } else {
            dim = comp.getPreferredSize();
            comp.setSize(dim);
        }
        Insets in = getBorderInsets(dim.width, dim.width);
        dim.width += in.left + in.right;
        dim.height += in.top + in.bottom;

        comp.setLocation(in.left, in.top);
        setPreferredSize(dim);
    }

    /**
     * 
     * @param c the component which this input-field should display. (or null)
     * @see #getInput()
     */
    public void setInput(JComponent c) {
        if (comp != null) {
            remove(comp);
        }
        if (c != null) {
            add(c);
        }
        comp = c;
    }

    /**
     * 
     * @return the component which this input-field is displaying. (or null)
     * @see #setInput(javax.swing.JComponent) 
     */
    public JComponent getInput() {
        return comp;
    }
}
