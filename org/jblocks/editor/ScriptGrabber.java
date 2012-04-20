package org.jblocks.editor;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.jblocks.scriptengine.Block;

/**
 * This class is for converting <code>AbstrBlock</code>s to executable code. <br />
 * 
 * @see org.jblocks.scriptengine
 * @author ZeroLuck
 */
public class ScriptGrabber {

    private ScriptGrabber() {
        // don't let anyone make an instance of this class.
    }
    
    /**
     * Creates code from a Script. <br />
     * 
     * @param hat the hat of the script
     * @param blockLib the installed blocks
     * @return the created code
     */
    public static Block[] getCodeFromScript(final AbstrBlock hat, final Map<String, Block> blockLib) {
        AbstrBlock[] pieces = JBlockSequence.getPuzzlePieces(((Puzzle) hat), PuzzleAdapter.TYPE_DOWN);
        Block[] b = new Block[pieces.length];
        
        for (int i = 0; i < b.length; i++) {
            b[i] = getCodeFromBlock(pieces[i], blockLib);
        }
        
        return b;
    }

    /**
     * Creates code from an AbstrBlock. <br />
     * 
     * @param block the AbstrBlock from which to grab the code
     * @param blockLib the installed blocks
     * @return the created code
     */
    public static Block getCodeFromBlock(final AbstrBlock block, final Map<String, Block> blockLib) {
        final String syntax = block.getBlockSyntax();
        Block b = blockLib.get(syntax);
        if (b == null) {
            throw new IllegalStateException("block for syntax '" + syntax + "' isn't available!");
        }
        b = b.clone();
        
        int parameter = 0;
        for (Component c : block.getComponents()) {
            if (c instanceof AbstrInput) {
                AbstrInput input = (AbstrInput) c;
                JComponent comp = input.getInput();
                if (comp != null) {
                    if (comp instanceof JTextField) {
                        b.setParameter(parameter, ((JTextField) comp).getText());
                    } else if (comp instanceof AbstrBlock) {
                        AbstrBlock paramBlock = (AbstrBlock) comp;
                        Block paramCode = getCodeFromBlock(paramBlock, blockLib);
                        // this should be fixed:
                        boolean doPreExec = !(syntax.equals("while %{b}%{br}%{s}"));

                        if (doPreExec) {
                            b.setParameter(parameter, paramCode);
                        } else {
                            b.setParameter(parameter, new Block[]{paramCode});
                        }
                    } else {
                        System.out.println("Warning: ScriptGrabber: What to do with: \"" + c + "\"?");
                    }
                }
                parameter++;
            } else if (c instanceof JBlockSequence) {
                JBlockSequence seq = (JBlockSequence) c;
                AbstrBlock[] stack = JBlockSequence.getPuzzlePieces((Puzzle) seq.getStack(), PuzzleAdapter.TYPE_TOP);
                Block[] codeSeq = new Block[stack.length];
                for (int i = 0; i < codeSeq.length; i++) {
                    codeSeq[i] = getCodeFromBlock(stack[i], blockLib);
                }
                b.setParameter(parameter, codeSeq);
                parameter++;
            }
        }
        return b;
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
