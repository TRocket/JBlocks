package org.jblocks.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
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
    private boolean borderEnabled = true;

    public AbstrInput() {
        setOpaque(false);
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
    protected abstract Insets getBorderInsets(int width, int height);

    /**
     * Draws the input's border. <br />
     * 
     * @param g the graphics object on which to draw.
     * @see #getBorderInsets(int, int) 
     */
    protected abstract void paintBlockBorder(Graphics g);

    /**
     * @return returns the size which is used when this input-field has no input.
     */
    public abstract Dimension getDefaultSize();

    /**
     * 
     * Sets the default input-field's input component. <br />
     */
    public abstract void reset();

    @Override
    public Dimension getPreferredSize() {
        if (!isValid()) {
            doLayout();
        }
        return super.getPreferredSize();
    }

    public JScriptPane getScriptPane() {
        Container cont = this;
        while ((cont = cont.getParent()) != null) {
            if (cont instanceof JScriptPane) {
                return (JScriptPane) cont;
            }
        }
        return null;
    }

    /**
     * 
     * Layouts the whole ScriptPane. <br />
     */
    protected void layoutRoot() {
        getScriptPane().validate();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        Insets border = getBorderInsets(getWidth(), getHeight());
        Color back = getBackground();

        Dimension size = getSize();
        Rectangle rect = new Rectangle(
                border.left,
                border.top,
                size.width - border.left - border.right,
                size.height - border.top - border.bottom);

        g.setColor(back);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);

        if (borderEnabled) {
            paintBlockBorder(g);
        }
    }

    /**
     * 
     * enables/disables the input's border. <br />
     * @see #setBorderEnabled(boolean) 
     */
    public void setBorderEnabled(boolean b) {
        if (borderEnabled != b) {
            borderEnabled = b;
            validate();
        }
    }

    /**
     * 
     * returns true if the border is enabled. <br />
     * @see #setBorderEnabled(boolean)  
     */
    public boolean isBorderEnabled() {
        return borderEnabled;
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
        if (borderEnabled) {
            Insets in = getBorderInsets(dim.width, dim.height);
            dim.width += in.left + in.right;
            dim.height += in.top + in.bottom;
            if (comp != null) {
                comp.setLocation(in.left, in.top);
            }
        } else {
            if (comp != null) {
                comp.setLocation(0, 0);
            }
        }

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

    /**
     * 
     * Finds a possible input for a Rectangle <br />
     * 
     * @see JScriptPane#getLocationOnScriptPane(javax.swing.JComponent)
     * @param cont the component to search.
     * @param r the rectangle on the JScriptPane.
     * @return null or an input.
     */
    static AbstrInput findInput(JComponent cont, Rectangle r, AbstrBlock b) {
        for (Component comp : cont.getComponents()) {
            if (comp == b) {
                continue;
            }
            if (comp instanceof AbstrInput) {
                AbstrInput inp = (AbstrInput) comp;
                Point p = JScriptPane.getLocationOnScriptPane(inp);
                Rectangle rect = new Rectangle(p, inp.getSize());
                if (rect.intersects(r) && inp.accepts(b)) {
                    return inp;
                }
            }
            if (comp instanceof JComponent) {
                AbstrInput inp = findInput((JComponent) comp, r, b);
                if (inp != null) {
                    return inp;
                }
            }
        }
        return null;
    }
}
