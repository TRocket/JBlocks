package org.jblocks.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import org.jblocks.JBlocks;

/**
 *
 * @author ZeroLuck
 */
public class JVariableInput extends AbstrInput {

    private VariableChooser chooser;

    public JVariableInput() {
        chooser = new VariableChooser();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean contains(int x, int y) {
        return true;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean accepts(AbstrBlock block) {
        return block instanceof JReporterBlock && !(block instanceof JBooleanBlock);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    protected Insets getBorderInsets(int width, int height) {
        return new Insets(0, 0, 0, 0);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    protected void paintBlockBorder(Graphics g) {
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Dimension getDefaultSize() {
        // shouldn't happen, there will always be an input
        return new Dimension(30, 20);
    }
 
    /**
     * {@inheritDoc} 
     */
    @Override
    public void reset() {
        setInput(chooser);
    }

    /**
     * Returns the value of this JVariableChooser. <br />
     * The value is an empty <code>String</code> if no variable were chosen. <br />
     * 
     * @see #setValue(java.lang.String) 
     */
    public String getValue() {
        return chooser.getValue();
    }

    /**
     * Sets the value of this JVariableChooser. <br />
     * <code>repaint()</code> and <code>validate()</code> are called. <br />
     * 
     * @see #getValue() 
     * @param val the new value to display. 
     */
    public void setValue(String val) {
        chooser.setValue(val);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        JScriptPane pane = getScriptPane();
        chooser.setEditable(!(pane == null || !pane.isDragEnabled()));
    }

    public static class VariableChooser extends JComponent {

        private static final int MIN_WIDTH = 35;
        private static final int MIN_HEIGHT = 20;
        private static final int RIGHT_GAP = 15;
        private static final int GAP = 2;
        // <member>
        private String currentSelected = "";
        private boolean editable = true;

        public VariableChooser() {
            addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent evt) {
                    if (evt.getX() > getWidth() - RIGHT_GAP && editable) {
                        openChooser(evt.getPoint());
                    }
                }
            });
        }

        private String[] getVariables() {
            List<String> variables = new ArrayList<String>();
            Map var = JBlocks.getContextForComponent(this).getScriptEngine().getGlobalVariables();
            for (Object o : var.keySet()) {
                if (o instanceof String) {
                    variables.add((String) o);
                }
            }
            return variables.toArray(new String[]{});
        }

        /**
         * Returns the block's JScriptPane or null if it hasn't one. <br />
         */
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
         * Makes this chooser editable or uneditable. <br />
         * 
         * @see #isEditable() 
         */
        public void setEditable(boolean b) {
            editable = b;
        }

        /**
         * Returns <code>true</code> if the chooser is editable. <br />
         * Otherwise <code>false</code> will be returned. <br />
         * 
         * @see #setEditable(boolean) 
         */
        public boolean isEditable() {
            return editable;
        }

        private void openChooser(Point p) {
            JPopupMenu menu = new JPopupMenu();
            for (final String s : getVariables()) {
                menu.add(s).addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        currentSelected = s;
                        doLayout();
                        getScriptPane().validate();
                        repaint();
                    }
                });
            }
            menu.show(this, p.x, p.y);
        }

        /**
         * {@inheritDoc} 
         */
        @Override
        public void doLayout() {
            setSize(getPreferredSize());
        }

        /**
         * Returns the value of this JVariableChooser. <br />
         * The value is an empty <code>String</code> if no variable were chosen. <br />
         * 
         * @see #setValue(java.lang.String) 
         */
        public String getValue() {
            return currentSelected;
        }

        /**
         * Sets the value of this JVariableChooser. <br />
         * <code>repaint()</code> and <code>validate()</code> are called. <br />
         * 
         * @see #getValue() 
         * @param val the new value to display. 
         */
        public void setValue(String val) {
            currentSelected = val;
            doLayout();
            getScriptPane().validate();
            repaint();
        }

        private Polygon getPlg() {
            final int width = 8;
            final int height = 4;
            final int x = getWidth() - 5 - width;
            final int y = getHeight() / 2 - height / 2;
            Polygon plg = new Polygon();
            plg.addPoint(x, y);
            plg.addPoint(x + width, y);
            plg.addPoint(x + width / 2, y + height);
            return plg;
        }

        /**
         * {@inheritDoc} 
         */
        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());

            return new Dimension(
                    Math.max(MIN_WIDTH, fm.stringWidth(currentSelected) + RIGHT_GAP + 2 * GAP),
                    Math.max(MIN_HEIGHT, fm.getHeight() + 2 * GAP));
        }

        /**
         * {@inheritDoc} 
         */
        @Override
        public void paintComponent(Graphics g) {
            final Color col = Colors.bright(getParent().getBackground(), 0.9f);
            final Dimension size = getSize();
            final FontMetrics fm = g.getFontMetrics();

            g.setColor(col);
            g.fillRect(0, 0, size.width, size.height);
            g.draw3DRect(0, 0, size.width - 1, size.height - 1, false);

            g.setColor(new Color(0x202020));
            g.fillPolygon(getPlg());

            g.setColor(Color.WHITE);
            g.drawString(currentSelected, GAP, fm.getAscent() + GAP);
        }
    }
}
