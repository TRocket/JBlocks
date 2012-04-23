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

    private BlockFactory() {
        // don't let anyone make an instance of this class
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

    public static String enquote(String s) {
        return s.replace("%", "%%");
    }

    /**
     * 
     * Creates a block from a String. <br />
     * Format: <br />
     * <ul>
     *  <li> <b>%{r}</b>                  : a reporter/boolean/text input. </li>
     *  <li> <b>%{b}</b>                  : a boolean input. </li>
     *  <li> <b>%{s}</b>                  : a sequence. </li>
     *  <li> <b>%{br}</b>                 : a new line. </li>
     *  <li> <b>%{gf}</b>                 : a green flag icon. </li>
     *  <li> <b>%{combo;ITEM_1}</b>       : a combo box. </li>
     *  <li> <b>%{var;NAME}</b>           : a variable. </li> 
     *  <li> <b>%%</b>                    : a '%' letter. </li>
     * </ul>
     * 
     * @throws IllegalArgumentException - if the type isn't available.
     * @param model the model for the new block
     * @return the created block
     */
    public static AbstrBlock createBlock(final BlockModel model) {
        final String type = model.getType();
        final String syntax = model.getSyntax();

        AbstrBlock block = null;
        if (type.equals("hat")) {
            block = new JHatBlock(model);
        } else if (type.equals("command")) {
            block = new JCommandBlock(model);
        } else if (type.equals("boolean")) {
            block = new JBooleanBlock(model);
        } else if (type.equals("reporter")) {
            block = new JReporterBlock(model);
        } else if (type.equals("cap")) {
            block = new JCapBlock(model);
        }
        if (block == null) {
            throw new IllegalArgumentException("\"" + type + "\" isn't a available block type.");
        }
        final Color textColor = Color.BLACK;
        final StringBuilder currentLabel = new StringBuilder();
        final int len = syntax.length();
        int off = 0;
        while (off < len) {
            final char c = syntax.charAt(off++);
            final char c2;
            if (off < len) {
                c2 = syntax.charAt(off);
            } else {
                c2 = 0;
            }
            if (c == '%') {
                if (c2 == '%') {
                    currentLabel.append('%');
                    off++;
                } else {
                    final String label = currentLabel.toString();
                    if (!label.trim().isEmpty()) {
                        final JLabel lab = new JLabel(label);
                        lab.setForeground(textColor);
                        block.add(lab);
                    }
                    currentLabel.delete(0, currentLabel.length());

                    if (syntax.charAt(off++) != '{') {
                        throw new IllegalArgumentException("format problem at char " + off + "!");
                    }
                    final StringBuilder fmt = new StringBuilder();
                    while (syntax.charAt(off) != '}') {
                        fmt.append(syntax.charAt(off++));
                    }
                    final String fmtString = fmt.toString();

                    addFmt0(block, fmtString);

                    off++;
                }
            } else {
                currentLabel.append(c);
            }
        }
        final String str = currentLabel.toString();
        if (!str.trim().isEmpty()) {
            JLabel lab = new JLabel(str);
            lab.setForeground(textColor);
            block.add(lab);
        }

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
     * Creates a new preview block. <br />
     * The preview block will have an {@link BlockModel#createPreviewModel(java.lang.String, java.lang.String) } BlockModel. <br />
     * 
     * @param type the type of the block
     * @param syntax the syntax of the block
     * @return the created block
     */
    public static AbstrBlock createBlock(String type, String syntax) {
        return createBlock(BlockModel.createPreviewModel(type, syntax));
    }

    /**
     * Creates a invisible new-line component. <br />
     * This is just formatting relevant. <br />
     */
    public static JComponent createNewLine() {
        return new NewLineComponent();
    }
}
