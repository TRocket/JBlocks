package org.jblocks.editor;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.jblocks.editor.AbstrBlock;
import org.jblocks.editor.AbstrInput;
import org.jblocks.editor.JBlockSequence;
import org.jblocks.editor.JScriptPane;
import org.jblocks.scriptengine.Block;

/**
 *
 * @author ZeroLuck
 */
public class ScriptGrabber {

    private ScriptGrabber() {
        // don't let anyone make an instance of this class.
    }

    /**
     * Creates code from an AbstrBlock. <br />
     * 
     * @param block the AbstrBlock from which to grab the code
     * @param blockLib the installed blocks
     * @return the created code
     */
    public static Block getCodeFromBlock(final AbstrBlock block, final Map<String, Block> blockLib) {
        Block code = null;
        Block b = blockLib.get(block.getBlockSyntax());
        if (b == null) {
            throw new IllegalStateException("block for syntax '" + b + "' isn't available!");
        }
        int parameter = 0;
        for (Component c : block.getComponents()) {
            if (c instanceof AbstrInput) {
                AbstrInput input = (AbstrInput) c;
                JComponent comp = input.getInput();
                if (comp == null) {
                    if (comp instanceof JTextField) {
                        b.setParameter(parameter, ((JTextField) comp).getText());
                    } else if (comp instanceof AbstrBlock) {
                        b.setParameter(parameter, getCodeFromBlock((AbstrBlock) comp, blockLib));
                    } else if (comp instanceof JBlockSequence) {
                        JBlockSequence seq = (JBlockSequence) comp;
                        AbstrBlock[] stack = JBlockSequence.getPuzzlePieces((Puzzle) seq.getStack(), PuzzleAdapter.TYPE_TOP);
                        Block[] codeSeq = new Block[stack.length];
                        for (int i = 0; i < codeSeq.length; i++) {
                            codeSeq[i] = getCodeFromBlock(stack[i], blockLib);
                        }
                        b.setParameter(parameter, codeSeq);
                    } else {
                        System.out.println("Warning: ScriptGrabber: What to do with: \"" + c + "\"?");
                    }
                }
                parameter++;
            }
        }

        return code;
    }

    /**
     * Creates code from a JScriptPane <br />
     * 
     * @param pane the JScriptPane from which to grab the scripts
     * @param blockLib the installed blocks
     * @return the grabbed scripts
     */
    public static Block[] getCodeFromEditor(JScriptPane pane, Map<String, Block> blockLib) {
        assert true : "Not implemented yet";

        List<Block> blocks = new ArrayList<Block>();
        // TODO

        return blocks.toArray(new Block[]{});
    }
}
