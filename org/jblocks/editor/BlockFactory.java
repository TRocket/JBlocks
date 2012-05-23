package org.jblocks.editor;

import java.awt.Color;
import java.awt.Font;
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

    // the green flag image
    private static BufferedImage greenflag;
    // blocks
    public static final String TYPE_CAP_BLOCK = "cap";
    public static final String TYPE_COMMAND_BLOCK = "command";
    public static final String TYPE_REPORTER_BLOCK = "reporter";
    public static final String TYPE_HAT_BLOCK = "hat";
    public static final String TYPE_BOOLEAN_BLOCK = "boolean";
    // inputs
    public static final String TYPE_REPORTER_INPUT = "reporter";
    public static final String TYPE_REPORTER_AND_TEXT_INPUT = "reporter,text";
    public static final String TYPE_BOOLEAN_INPUT = "boolean";
    public static final String TYPE_SEQUENCE_INPUT = "sequence";
    public static final String TYPE_VARIABLE_INPUT = "variable";

    static {
        greenflag = JBlocks.getImage("greenFlag.png");
    }

    private BlockFactory() {
        // don't let anyone make an instance of this class
    }

    private static void addFormatComponent(AbstrBlock block, String fmt) {
        String[] s = fmt.split(";");
        if (s.length < 1) {
            throw new IllegalArgumentException("parse error in bs.");
        }
        if (s[0].equals("r")) {
            JReporterInput inp = new JReporterInput();
            inp.setType(JReporterInput.TYPE_REPORTER);
            inp.setBackground(block.getBackground());
            inp.reset();
            block.add(inp);
        } else if (s[0].equals("t")) {
            JReporterInput inp = new JReporterInput();
            inp.setType(JReporterInput.TYPE_TEXT_AND_REPORTER);
            inp.setBackground(block.getBackground());
            inp.reset();
            block.add(inp);
        } else if (s[0].equals("v")) {
            JVariableInput ch = new JVariableInput();
            ch.setBackground(block.getBackground());
            ch.reset();
            block.add(ch);
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

    public static String enquote(String s) {
        return s.replace("%", "%%");
    }

    /**
     * Returns the count of parameters of a syntax. <br />
     * 
     * @param s the syntax
     * @return the count of parameters of this syntax
     */
    public static int countParameters(String s) {
        int cnt = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '%' && (i + 1 >= s.length() || s.charAt(i + 1) != '%')) {
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * 
     * Creates a block from a String. <br />
     * Format: <br />
     * <ul>
     *  <li> <b>%{r}</b>                  : a reporter/boolean. </li>
     *  <li> <b>%{t}</b>                  : a reporter/boolean/text input. </li>
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
        final Color textColor = Color.BLACK;
        
        final String type = model.getType();
        final String syntax = model.getSyntax();
        final AbstrBlock block = createBlock(type);
        block.setModel(model);

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
                        final JComponent lab = new JLabel(label);
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

                    addFormatComponent(block, fmtString);

                    off++;
                }
            } else {
                currentLabel.append(c);
            }
        }
        final String str = currentLabel.toString();
        if (!str.trim().isEmpty()) {
            JComponent lab = new JLabel(str);
            lab.setForeground(textColor);
            block.add(lab);
        }

        return block;
    }

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
        } else if (type.equals(TYPE_VARIABLE_INPUT)) {
            return new JVariableInput();
        } else if (type.equals(TYPE_BOOLEAN_INPUT)) {
            return new JBooleanInput();
        } else if (type.equals(TYPE_SEQUENCE_INPUT)) {
            return new JBlockSequence();
        } else {
            throw new IllegalArgumentException("\"" + type + "\" isn't an available input type.");
        }
    }

    private static AbstrBlock createBlock(String type) {
        AbstrBlock block = null;
        if (type.equals(TYPE_HAT_BLOCK)) {
            block = new JHatBlock(null);
        } else if (type.equals(TYPE_COMMAND_BLOCK)) {
            block = new JCommandBlock(null);
        } else if (type.equals(TYPE_BOOLEAN_BLOCK)) {
            block = new JBooleanBlock(null);
        } else if (type.equals(TYPE_REPORTER_BLOCK)) {
            block = new JReporterBlock(null);
        } else if (type.equals(TYPE_CAP_BLOCK)) {
            block = new JCapBlock(null);
        }
        if (block == null) {
            throw new IllegalArgumentException("\"" + type + "\" isn't a available block type.");
        }
        return block;
    }

    /**
     * Creates a new preview block. <br />
     * The preview block will have an {@link BlockModel#createPreviewModel(java.lang.String, java.lang.String) } BlockModel. <br />
     * 
     * @param type the type of the block
     * @param syntax the syntax of the block
     * @return the created block
     */
    public static AbstrBlock createPreviewBlock(String type, String syntax) {
        return createBlock(BlockModel.createPreviewModel(type, syntax));
    }

    /**
     * Creates a invisible new-line component. <br />
     * This is just formatting relevant. <br />
     */
    public static JComponent createNewLine() {
        return new NewLineComponent();
    }

    public static String createInputFormat(String type) {
        if (type.equals(TYPE_REPORTER_INPUT)) {
            return "%{r}";
        } else if (type.equals(TYPE_REPORTER_AND_TEXT_INPUT)) {
            return "%{t}";
        } else if (type.equals(TYPE_VARIABLE_INPUT)) {
            return "%{v}";
        } else if (type.equals(TYPE_BOOLEAN_INPUT)) {
            return "%{b}";
        } else if (type.equals(TYPE_SEQUENCE_INPUT)) {
            return "%{s}";
        } else {
            throw new IllegalArgumentException("\"" + type + "\" isn't an available input type.");
        }
    }
}
