package org.jblocks.editor;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.jblocks.editor.JVariableInput.VariableChooser;
import org.jblocks.scriptengine.Block;

/**
 * This class is for converting <code>AbstrBlock</code>s to executable code. <br />
 * 
 * @see BlockIO
 * @see org.jblocks.scriptengine
 * @author ZeroLuck
 */
public class ScriptToCode {

    private ScriptToCode() {
        // don't let anyone make an instance of this class.
    }

    /**
     * Creates code from a Script. <br />
     * 
     * @param hat the hat of the script
     * @param blockLib the installed blocks
     * @return the created code
     */
    public static Block[] getCodeFromScript(final AbstrBlock hat) {
        AbstrBlock[] pieces = JBlockSequence.getPuzzlePieces(((Puzzle) hat), PuzzleAdapter.TYPE_DOWN);
        Block[] b = new Block[pieces.length];

        for (int i = 0; i < b.length; i++) {
            b[i] = getCodeFromBlock(pieces[i]);
        }

        return b;
    }

    /**
     * Creates code from an AbstrBlock. <br />
     * 
     * @param block the AbstrBlock from which to grab the code
     * @return the created code
     */
    public static Block getCodeFromBlock(final AbstrBlock block) {
        final BlockModel model = block.getModel();

        final String syntax = model.getSyntax();
        Block b = model.getCode();
        if (b == null) {
            throw new IllegalStateException("block for syntax '" + syntax + "' has no code!");
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
                    } else if (comp instanceof VariableChooser) {
                        b.setParameter(parameter, ((VariableChooser) comp).getValue());
                    } else if (comp instanceof AbstrBlock) {
                        AbstrBlock paramBlock = (AbstrBlock) comp;
                        Block paramCode = getCodeFromBlock(paramBlock);
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
                AbstrBlock[] stack = JBlockSequence.getPuzzlePieces((Puzzle) seq.getStack(), PuzzleAdapter.TYPE_DOWN);
                Block[] codeSeq = new Block[stack.length];
                for (int i = 0; i < codeSeq.length; i++) {
                    codeSeq[i] = getCodeFromBlock(stack[i]);
                }
                b.setParameter(parameter, codeSeq);
                parameter++;
            }
        }
        return b;
    }
}
