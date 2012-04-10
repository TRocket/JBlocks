package org.jblocks.editor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.jblocks.JBlocks;

/**
 *
 * @author ZeroLuck
 */
public class BlockFactory {

    private static BufferedImage greenflag;

    static {
        try {
            greenflag = ImageIO.read(JBlocks.class.getResourceAsStream("res/goButton.gif"));
        } catch (IOException ex) {
            throw new java.lang.ExceptionInInitializerError(ex);
        }
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
}
