package org.jblocks.editor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.jblocks.JBlocks;

/**
 *
 * @author ZeroLuck
 */
public class BlockFactory {

    private static BufferedImage greenflag;

    static {
        greenflag = JBlocks.getImage("greenFlag.png");
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
        } else if (s[0].equals("var")) {
            block.add(new JVariableInput(s[1]));
        }
    }
    public static final String TYPE_CAP_BLOCK = "cap";
    public static final String TYPE_CONTROL_BLOCK = "control";
    public static final String TYPE_REPORTER_BLOCK = "reporter";
    public static final String TYPE_HAT_BLOCK = "hat";

    /**
     * 
     * Creates a block from a String. <br />
     * Format: <br />
     *  - %{r}                  : a reporter/boolean/text input. <br />
     *  - %{b}                  : a boolean input. <br />
     *  - %{s}                  : a sequence.
     *  - %{br}                 : a new line.
     *  - %{gf}                 : a green flag icon. <br />
     *  - %{combo;ITEM_1}       : a combo box. <br />
     *  - %{var;NAME}           : a variable. <br /> 
     * <br />
     * 
     * @throws IllegalArgumentException - if the type isn't available.
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
            throw new IllegalArgumentException("\"" + type + "\" isn't a available block type.");
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
    public static final String TYPE_REPORTER_INPUT = "reporter";
    public static final String TYPE_REPORTER_AND_TEXT_INPUT = "reporter,text";
    public static final String TYPE_BOOLEAN_INPUT = "boolean";
    public static final String TYPE_SEQUENCE_INPUT = "sequence";

    /**
     * Creates a input from a string. <br />
     * 
     * 
     * @throws IllegalArgumentException - if the type isn't available.
     * @param type see class constants
     * @return the created input
     */
    public static JComponent createInput(String type) {
        if (type.equals(TYPE_REPORTER_INPUT)) {
            JReporterInput inp = new JReporterInput();
            inp.setType(JReporterInput.TYPE_REPORTER);
            inp.reset();
            return inp;
        } else if (type.equals(TYPE_REPORTER_AND_TEXT_INPUT)) {
            JReporterInput inp = new JReporterInput();
            inp.setType(JReporterInput.TYPE_TEXT_AND_REPORTER);
            inp.reset();
            return inp;
        } else if (type.equals(TYPE_BOOLEAN_INPUT)) {
            return new JBooleanInput();
        } else if (type.equals(TYPE_SEQUENCE_INPUT)) {
            return new JBlockSequence();
        } else {
            throw new IllegalArgumentException("\"" + type + "\" isn't a available input type.");
        }
    }

    /**
     * Creates a invisible new-line component. <br />
     * This is just formatting relevant. <br />
     */
    public static JComponent createNewLine() {
        return new NewLineComponent();
    }
}
