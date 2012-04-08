package org.jblocks.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.jblocks.JBlocks;

/**
 *
 * A ScriptPane for the BlockEditor. <br />
 * 
 * @version 0.3
 * @author ZeroLuck
 */
public class JScriptPane extends JPanel {

    private static BufferedImage scriptpane;
    private static BufferedImage greenflag;

    static {
        try {
            scriptpane = ImageIO.read(JBlocks.class.getResourceAsStream("res/scriptpane.png"));
            greenflag = ImageIO.read(JBlocks.class.getResourceAsStream("res/goButton.gif"));
        } catch (IOException ex) {
            throw new java.lang.ExceptionInInitializerError(ex);
        }
    }
    private static final int CLEANUP_LEFT = 10;
    private static final int CLEANUP_SPACE = 5;
    private static final int CLEANUP_TOP = 10;
    // <member>
    private Image scrp = scriptpane;

    public JScriptPane() {
        this(true);
    }

    public JScriptPane(boolean clean) {
        setBackground(Color.WHITE);
        setLayout(null);

        if (clean) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem item = new JMenuItem("cleanup");
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    cleanup();
                }
            });
            menu.add(item);
            setComponentPopupMenu(menu);
        }
    }

    public Image getScriptPaneImage() {
        return scrp;
    }

    public void setScriptPaneImage(Image img) {
        if (img == null) {
            throw new IllegalArgumentException("img is null!");
        }
        scrp = img;
    }

    private static void addFmt0(AbstrBlock block, String fmt) {
        String[] s = fmt.split(";");
        if (s.length < 1) {
            throw new IllegalArgumentException("parse error in bs.");
        }
        if (s[0].equals("r")) {
            JReporterInput inp = new JReporterInput();
            inp.setBackground(block.getBackground());
            inp.reset();
            block.add(inp);
        } else if (s[0].equals("b")) {
            JBooleanInput inp = new JBooleanInput();
            inp.setBackground(block.getBackground());
            inp.reset();
            block.add(inp);
        } else if (s[0].equals("gf")) {
            block.add(new JLabel(new ImageIcon(greenflag)));
        } else if (s[0].equals("combo")) {
            JComboBox<String> box = new JComboBox<String>();
            for (int i = 1; i < s.length; i++) {
                box.addItem(s[i]);
            }
            block.add(box);
        } else if (s[0].equals("s")) {
            JBlockSequence seq = new JBlockSequence();
            block.add(seq);
        } else if (s[0].equals("br")) {
            block.add(new NewLineComponent());
        }
    }

    /**
     * 
     * Creates a block from a String. <br />
     * Format: <br />
     *  - %{r}    : a reporter/boolean/text input. <br />
     *  - %{b}                  : a boolean input. <br />
     *  - %{s}                  : a sequence.
     *  - %{br}                 : a new line.
     *  - %{gf}                 : a green flag icon. <br />
     *  - %{combo;ITEM_1}       : a combo box. <br />
     * <br />
     * 
     * @param type "control", "cap", "reporter", "boolean" or "hat"
     * @param bs the format for the block label
     * @return the created block
     */
    public static AbstrBlock createBlock(String type, String bs) {
        AbstrBlock block = null;
        if (type.equals("hat")) {
            block = new JHatBlock();
        } else if (type.equals("command")) {
            block = new JCommandBlock();
        } else if (type.equals("boolean")) {
            block = new JBooleanBlock();
        } else if (type.equals("reporter")) {
            block = new JReporterBlock();
        } else if (type.equals("cap")) {
            block = new JCapBlock();
        }
        if (block == null) {
            throw new IllegalArgumentException("\"" + type + "\" isn't a correct block type.");
        }
        final Color textColor = Color.BLACK;
        StringBuilder sb = new StringBuilder();
        int off = 0; 
        int len = bs.length();
        while (off < len) {
            char c = bs.charAt(off++);
            if (c == '%') {
                String str = sb.toString();
                if (!str.trim().isEmpty()) {
                    JLabel lab = new JLabel(str);
                    lab.setForeground(textColor);
                    block.add(lab);
                }
                sb.delete(0, sb.length());
                if (bs.charAt(off++) != '{') {
                    throw new IllegalArgumentException("format problem at char " + off + "!");
                }
                StringBuilder fmt = new StringBuilder();
                while (bs.charAt(off) != '}') {
                    fmt.append(bs.charAt(off));
                    off++;
                }
                addFmt0(block, fmt.toString());
                off++;
            } else {
                sb.append(c);
            }
        }
        String str = sb.toString();
        if (!str.trim().isEmpty()) {
            JLabel lab = new JLabel(str);
            lab.setForeground(textColor);
            block.add(lab);
        }

        block.setBlockType(type);
        block.setBlockSyntax(bs);
        return block;
    }

    // FIXME: replace the "-6"s.
    private static int doPuzzleH(Puzzle p, int y, Set<Component> set) {
        int h = 0;
        AbstrBlock[] up = JBlockSequence.getPuzzlePieces(p, PuzzleAdapter.TYPE_TOP);
        AbstrBlock[] down = JBlockSequence.getPuzzlePieces(p, PuzzleAdapter.TYPE_DOWN);

        up[up.length - 1].setLocation(CLEANUP_LEFT, y);
        ((Puzzle) up[up.length - 1]).layoutPuzzle();
        for (AbstrBlock b : up) {
            set.add(b);
            h += b.getHeight();
            if (b instanceof JCommandBlock || b instanceof JHatBlock) {
                h -= 6;
            }
        }

        for (AbstrBlock b : down) {
            if (!set.contains(b)) {
                set.add(b);
                h += b.getHeight();
                if (b instanceof JCommandBlock || b instanceof JHatBlock) {
                    h -= 6;
                }
            }
        }
        h += 6;


        return h;
    }

    /**
     *
     * Layouts the scripts like in Scratch. <br />
     */
    public void cleanup() {
        doLayout();
        HashSet<Component> set = new HashSet<Component>(25);

        int y = CLEANUP_TOP;

        for (Component c : getComponents()) {
            if (!set.contains(c)) {
                if (c instanceof Puzzle) {
                    y += doPuzzleH((Puzzle) c, y, set) + CLEANUP_SPACE;
                } else {
                    set.add(c);
                    Dimension size = c.getSize();
                    c.setLocation(CLEANUP_LEFT, y);
                    y += size.height + CLEANUP_SPACE;
                }
            }
        }
    }
    // <member>
    private boolean drag = true;

    public void setDragEnabled(boolean b) {
        drag = b;
    }

    public boolean isDragEnabled() {
        return drag;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        for (Component c : getComponents()) {
            Dimension preferred = c.getPreferredSize();
            if (!c.getSize().equals(preferred)) {
                c.setSize(preferred);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        int w = 0, h = 0;
        for (Component c : getComponents()) {
            Point p = c.getLocation();
            Dimension size = c.getPreferredSize();
            if (p.x + size.width > w) {
                w = p.x + size.width;
            }
            if (p.y + size.height > h) {
                h = p.y + size.height;
            }
        }

        return new Dimension(w + 10, h + 100);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        Image back = scrp;
        Rectangle clip = g.getClipBounds();
        int w = back.getWidth(null);
        int h = back.getHeight(null);

        for (int x = clip.x - (clip.x % w); x < (clip.x + clip.width); x += w) {
            for (int y = clip.y - (clip.y % h); y < (clip.y + clip.height); y += h) {
                g.drawImage(back, x, y, this);
            }
        }

    }

    /**
     * 
     * @param c the component
     * @return the location of the component on the script pane.
     */
    public static Point getLocationOnScriptPane(JComponent c) {
        Point p = new Point();
        Container cont = c;
        while (cont != null) {
            p.x += cont.getX();
            p.y += cont.getY();
            cont = cont.getParent();
            if (cont instanceof JScriptPane) {
                break;
            }
        }
        return p;
    }
}
