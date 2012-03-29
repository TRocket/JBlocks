package org.jblocks.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
    private static int CATEGORY_WIDTH = 75;
    private static int CATEGORY_LEFT = 10;
    private static int CATEGORY_RIGHT = 5;
    private static int CATEGORY_BOTTOM = 5;
    private static int DEFAULT_SPACE = 5;
    // <member>
    private int cpline;

    /**
     * 
     * @param categories_per_line - the count of the categories per line.
     */
    public JCategoryChooser(int categories_per_line) {
        cpline = categories_per_line;
        setBackground(new Color(0xB0B0B0));
    }

    /**
     * 
     * This constructor is the same like JCategoryChooser(2). <br />
     */
    public JCategoryChooser() {
        this(2);
    }

    public JComponent addCategory(String name, Color c) {
        Category ctg = new Category(name);
        ctg.setBackground(c);
        add(ctg);

        return ctg;
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
            if (cnt >= cpline) {
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

    private static class Category extends JComponent {

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
                    clicked = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent me) {
                    clicked = false;
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

        private static Color bright(Color c, float f) {
            return new Color(Math.min((int) (c.getRed() * f), 255),
                    Math.min((int) (c.getGreen() * f), 255),
                    Math.min((int) (c.getBlue() * f), 255));
        }

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
}
