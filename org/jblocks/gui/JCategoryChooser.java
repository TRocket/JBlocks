package org.jblocks.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * A GUI class for choosing block-categoríes. <br />
 * (like in Scratch) <br />
 * 
 * @author ZeroLuck
 */
public class JCategoryChooser extends JPanel {

    // <global>
    private static int CATEGORY_WIDTH = 85;
    private static int CATEGORY_LEFT = 10;
    private static int CATEGORY_RIGHT = 5;
    private static int CATEGORY_BOTTOM = 5;
    private static int DEFAULT_SPACE = 5;
    // <member>
    private int columns;
    private Category current;
    // <listeners>
    public static interface CategoryChooserSelectionListener extends EventListener {
        
        public void categorySelected(String name);
    }

    /**
     * Creates a new <code>JCategoryChooser</code> with <code>n</code> columns. <br />
     * 
     * @see #JCategoryChooser()
     * @param categories_per_line the count of columns
     */
    public JCategoryChooser(int categories_per_line) {
        columns = categories_per_line;
    }
    
    /**
     * Adds the specified <code>CategoryChooserSelectionListener</code> to this <code>JCategoryChooser</code>.
     * @param c the listener
     */
    public void addCategoryChooserSelectionListener(CategoryChooserSelectionListener c) {
        this.listenerList.add(CategoryChooserSelectionListener.class, c);
    }
    
    /**
     * Removes the specified <code>CategoryChooserSelectionListener</code> from this <code>JCategoryChooser</code>.
     * @param c the listener
     */
    public void removeCategoryChooserSelectionListener(CategoryChooserSelectionListener c) {
        this.listenerList.remove(CategoryChooserSelectionListener.class, c);
    }

    /**
     * Creates a new JCategoryChooser with 2 columns. <br />
     */
    public JCategoryChooser() {
        this(2);
    }

    /**
     * Adds a new category to this <code>JCategoryChooser</code>. <br />
     * 
     * @param name the name of the new category
     * @param c the color of the new category
     */
    public void addCategory(final String name, final Color c) {
        final Category ctg = new Category(name);
        ctg.setBackground(c);
        add(ctg);
        if (current == null) {
            current = ctg;
            ctg.clicked = true;
        }
    }
    
    /**
     * Returns the categories of this JCategoryChooser. <br />
     * 
     * @see #addCategory(java.lang.String, java.awt.Color) 
     * @see #removeCategory(java.lang.String) 
     */
    public String[] getCategories() {
        final List<String> categories = new ArrayList<String>();
        for (final Component c : getComponents()) {
            if (c instanceof Category) {
                final Category ctg = (Category) c;
                categories.add(ctg.text);
            }
        }
        return categories.toArray(new String[]{});
    }

    /**
     * Returns the color of a specified category. <br />
     * If the category doesn't exists <code>null</code> is returned. <br />
     * 
     * @param name the name of the category
     * @return the color of the category or null
     */
    public Color getCategoryColorForName(String name) {
        for (final Component c : getComponents()) {
            if (c instanceof Category) {
                final Category ctg = (Category) c;
                if (ctg.text.equals(name))
                    return ctg.getBackground();
            }
        }
        return null;
    }
    
    /**
     * Removes the category with the specified name. <br />
     * 
     * @see #getCategories() 
     * @see #addCategory(java.lang.String, java.awt.Color) 
     */
    public void removeCategory(final String name) {
        for (final Component c : getComponents()) {
            if (c instanceof Category) {
                final Category ctg = (Category) c;
                if (ctg.text.equals(name)) {
                    remove(ctg);
                }
            }
        }
        validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        if (!isValid()) {
            doLayout();
        }
        return super.getPreferredSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        int cnt = 0;
        int maxX = DEFAULT_SPACE * 2;
        int maxY = DEFAULT_SPACE;
        int xoff = DEFAULT_SPACE;
        int lineH = 0;
        for (Component comp : getComponents()) {
            Dimension size = comp.getPreferredSize();
            comp.setSize(size);
            comp.setLocation(xoff, maxY);
            if (size.height > lineH) {
                lineH = size.height;
            }
            xoff += size.width + DEFAULT_SPACE;
            cnt++;
            if (cnt >= columns) {
                if (xoff > maxX) {
                    maxX = xoff;
                }
                cnt = 0;
                xoff = DEFAULT_SPACE;
                maxY += DEFAULT_SPACE + lineH;
                lineH = 0;
            }
        }
        if (xoff > DEFAULT_SPACE && xoff > maxX) {
            maxX = xoff;
        }
        maxX += DEFAULT_SPACE;
        maxY += DEFAULT_SPACE + lineH;
        
        setPreferredSize(new Dimension(maxX, maxY));
    }
    
    private void select(Category c) {
        c.clicked = true;
        if (current != null) {
            current.clicked = false;
            current.repaint();
        }
        current = c;
        c.repaint();
        
        for (CategoryChooserSelectionListener listener : 
                this.listenerList.getListeners(CategoryChooserSelectionListener.class)) {
            
            listener.categorySelected(c.text);
        }
    }
    
    private class Category extends JComponent {
        
        private String text;
        private boolean clicked = false;
        private boolean highlight = false;
        
        public Category(String txt) {
            if (txt == null) {
                throw new IllegalArgumentException("text is null!");
            }
            text = txt;
            
            this.addMouseListener(new MouseListener() {
                
                @Override
                public void mouseClicked(MouseEvent me) {
                }
                
                @Override
                public void mousePressed(MouseEvent me) {
                    select(Category.this);
                }
                
                @Override
                public void mouseReleased(MouseEvent me) {
                    repaint();
                }
                
                @Override
                public void mouseEntered(MouseEvent me) {
                    highlight = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent me) {
                    highlight = false;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                }
            });
        }

        /**
         *  {@inheritDoc}
         */
        @Override
        public void paintComponent(Graphics grp) {
            Graphics2D g = (Graphics2D) grp;
            
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            
            Dimension size = getSize();
            Color col = getBackground();
            Color shadow = col.darker();
            
            Color labelCol = Color.GRAY;
            Color labelShadow = Color.DARK_GRAY;
            if (highlight) {
                labelCol = Color.LIGHT_GRAY;
                labelShadow = Color.GRAY;
            }
            if (clicked) {
                labelCol = bright(col, 0.8F);
                labelShadow = bright(shadow, 0.8F);
            }
            
            Stroke basic = g.getStroke();

            // LEFT
            g.setPaint(new java.awt.GradientPaint(
                    CATEGORY_LEFT, size.height / 2, col,
                    0, size.height / 2, shadow));
            
            g.fillOval(0, 0, CATEGORY_LEFT * 2, size.height);

            // BOTTOM
            g.setPaint(new java.awt.GradientPaint(
                    CATEGORY_LEFT, size.height - CATEGORY_BOTTOM, labelCol,
                    CATEGORY_LEFT, size.height, labelShadow));
            
            g.fillRect(CATEGORY_LEFT, size.height - CATEGORY_BOTTOM,
                    size.width - CATEGORY_LEFT - CATEGORY_RIGHT, CATEGORY_BOTTOM);

            // RIGHT
            g.setPaint(new java.awt.GradientPaint(size.width - CATEGORY_RIGHT, 0, labelCol, size.width, 0, labelShadow));
            g.fillOval(size.width - CATEGORY_RIGHT * 2, 0, CATEGORY_RIGHT * 2, size.height);

            // MIDDLE
            g.setColor(labelCol);
            g.fillRect(CATEGORY_LEFT, 0, size.width - CATEGORY_LEFT - CATEGORY_RIGHT, size.height - CATEGORY_BOTTOM);

            // TEXT
            g.setColor(Color.WHITE);
            g.setFont(getFont());
            g.drawString(text, CATEGORY_LEFT + 5, size.height - CATEGORY_BOTTOM);
            
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            
            g.setStroke(basic);
        }
        
        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            Dimension dim = new Dimension();
            dim.width += CATEGORY_LEFT + CATEGORY_RIGHT + CATEGORY_WIDTH;
            dim.height += CATEGORY_BOTTOM + fm.getHeight();
            
            return dim;
        }
    }
    
    private static Color bright(Color c, float f) {
        return new Color(Math.min((int) (c.getRed() * f), 255),
                Math.min((int) (c.getGreen() * f), 255),
                Math.min((int) (c.getBlue() * f), 255));
    }
}
