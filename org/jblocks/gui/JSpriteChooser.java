package org.jblocks.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;

/**
 *
 * A sprite chooser like in Scratch. <br />
 * TODO: add java-doc. <br />
 * 
 * @version 1.0
 * @author ZeroLuck
 */
public class JSpriteChooser extends JComponent {

    // <global>
    private static final int SV_LEFT = 10;
    private static final int SV_RIGHT = 10;
    private static final int SV_TOP = 10;
    private static final int SV_BOTTOM = 10;
    private static final int SV_WIDTH = 60;
    private static final int SV_HEIGHT = 70;
    private static final int DEFAULT_SPACE = 5;
    // <member>

    /**
     * creates a new <b>empty</b> sprite-chooser. <br />
     */
    public JSpriteChooser() {
        super();
        setBackground(Color.GRAY);
    }

    public JComponent addSpriteView(Object key, String text, Image img) {
        SpriteView v = new SpriteView(key, text, img);
        if (getComponentCount() == 0) {
            add(v);
            selectSpriteView(v);
        } else {
            add(v);
        }
        return v;
    }

    public void removeSpriteView(Object key) {
        for (Component c : getComponents()) {
            if (c instanceof SpriteView) {
                SpriteView view = (SpriteView) c;
                if (view.key == key) {
                    remove(view);
                    return;
                }
            }
        }
    }

    public void setSpriteViewData(Object key, String text, Image img) {
        for (Component c : getComponents()) {
            if (c instanceof SpriteView) {
                SpriteView view = (SpriteView) c;
                if (view.key == key) {
                    view.img = img;
                    view.text = text;
                    view.repaint();
                    return;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        Dimension size = getSize();
        int lineH = 0;
        int yoff = DEFAULT_SPACE;
        int xoff = DEFAULT_SPACE;
        for (Component c : getComponents()) {
            Dimension dim = c.getPreferredSize();
            c.setSize(dim);

            if (xoff + dim.width + DEFAULT_SPACE > size.width) {
                yoff += lineH + DEFAULT_SPACE;
                xoff = DEFAULT_SPACE;
                lineH = 0;
            }
            if (dim.height > lineH) {
                lineH = dim.height;
            }
            c.setLocation(xoff, yoff);
            xoff += dim.width + DEFAULT_SPACE;
        }
        xoff += DEFAULT_SPACE;
        yoff += DEFAULT_SPACE + lineH;

        setPreferredSize(new Dimension(Math.max(xoff, 270), yoff));
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

    private void selectSpriteView(SpriteView v) {
        for (Component c : getComponents()) {
            if (c instanceof SpriteView) {
                SpriteView view = (SpriteView) c;
                if (view.selected) {
                    view.selected = false;
                    view.repaint();
                }
            }
        }
        v.selected = true;
        v.repaint();
    }

    private static String filterLength(String str, FontMetrics fm) {
        if (fm.stringWidth(str) < SV_WIDTH - SV_RIGHT) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        int len = str.length();
        int i;
        for (i = 0; i < len && fm.stringWidth(sb.toString() + "...") < SV_WIDTH - SV_RIGHT; i++) {
            sb.append(str.charAt(i));
        }
        if (i < len) {
            sb.append("...");
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private final class SpriteView extends JComponent {

        private String text;
        private Image img;
        private boolean highlight;
        private boolean selected;
        private Object key;

        /**
         * Creates a new SpriteView for the JSpriteChooser. <br />
         * 
         * @throws IllegalArgumentException - if txt or img is null.
         * @param txt - the text to display.
         * @param img - the image to display.
         * @param key - the key of this sprite-view.
         */
        public SpriteView(Object key, String txt, Image img) {
            this.key = key;
            setText(txt);
            setImage(img);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent me) {
                }

                @Override
                public void mousePressed(MouseEvent me) {
                    selectSpriteView(SpriteView.this);
                }

                @Override
                public void mouseReleased(MouseEvent me) {
                }

                @Override
                public void mouseEntered(MouseEvent me) {
                    highlight = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent me) {
                    highlight = false;
                    repaint();
                }
            });
        }

        /**
         * Sets the view's image. <br />
         * 
         * @param img - the image to display.
         * @throws IllegalArgumentException - if img is null.
         */
        public void setImage(Image img) {
            if (img == null) {
                throw new IllegalArgumentException("img is null!");
            }
            this.img = img;
        }

        /**
         * Sets the view's text. <br />
         * 
         * @param txt - the text to display.
         * @throws IllegalArgumentException - if txt is null.
         */
        public void setText(String txt) {
            if (txt == null) {
                throw new IllegalArgumentException("txt is null!");
            }
            this.text = txt;
            this.setToolTipText(txt);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paintComponent(Graphics grp) {
            Graphics2D g = (Graphics2D) grp;

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Color lightBlue = Color.CYAN;
            Color col = getBackground();
            Stroke basic = g.getStroke();

            g.setColor(col);
            g.fillRect(0, 0, SV_WIDTH + SV_LEFT + SV_RIGHT,
                    SV_HEIGHT + SV_TOP + SV_BOTTOM);

            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();

            g.drawImage(img, SV_LEFT, SV_TOP, SV_WIDTH , SV_HEIGHT - fm.getHeight(), this);
            g.setColor(Color.WHITE);

            String name = filterLength(text, fm);
            g.drawString(name, SV_LEFT + (SV_WIDTH / 2 - fm.stringWidth(name) / 2), SV_TOP + SV_HEIGHT);

            if (selected) {
                g.setColor(lightBlue);
            } else if (highlight) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(Color.LIGHT_GRAY);
            }
            g.setStroke(new BasicStroke(2, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
            g.drawRoundRect(0, 0, SV_LEFT + SV_WIDTH + SV_RIGHT - 1,
                    SV_TOP + SV_HEIGHT + SV_BOTTOM -1, 10, 10);



            g.setStroke(basic);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(SV_LEFT + SV_RIGHT + SV_WIDTH,
                    SV_TOP + SV_BOTTOM + SV_HEIGHT);
        }
    }
}
