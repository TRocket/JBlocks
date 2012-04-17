package org.jblocks.scriptengine.impl;

import java.util.Arrays;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.IScript;
import org.jblocks.scriptengine.IScriptEngine;
import org.jblocks.scriptengine.impl.DefaultScriptThread.StackElement;

/**
 *
 * @author ZeroLuck
 */
public class Test {

    public static void main(String[] args) {
        IScriptEngine eng = new DefaultScriptEngine();

        NativeBlock var = new NativeBlock(0) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                System.out.println("Params: " + Arrays.toString(ctx.parent.param));
                return null;
            }
        };
        NativeBlock ret = new NativeBlock(0) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                return "Hallo Welt aus Deutschland";
            }
        };
        NativeBlock msg = new NativeBlock(0) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                System.out.println("Nachricht");
                return null;
            }
        };
        NativeBlock eq = new NativeBlock(2) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                return params[0].equals(params[1]);
            }
        };

        NativeBlock forBlock = DefaultBlocks.FOR;
        NativeBlock retBlock = DefaultBlocks.RETURN;
        NativeBlock ifBlock = DefaultBlocks.IF;
        
        retBlock.setParameter(0, "<Return Wert>");
        
        eq.setParameter(0, "Hallo Welt aus Deutschland");
        eq.setParameter(1, ret);
    //    ifBlock.setParameter(0, eq);
        ifBlock.setParameter(0, new Block[]{msg, msg, msg});
        
        ByobBlock seq = new ByobBlock(1, new Block[]{ var, retBlock, msg});
        seq.setParameter(0, "Hallo Sequence Parameter");

        forBlock.setParameter(0, new Block[]{seq});
        forBlock.setParameter(1, 3);

        IScript script = eng.compile(new Block[]{ifBlock, forBlock});
        for (int i = 0; i < 1; i++) {
            eng.execute(script);
        }
    }
}
