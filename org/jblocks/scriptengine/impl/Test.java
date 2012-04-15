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
        NativeBlock nb = new NativeBlock(2) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                System.out.println(Arrays.toString(params));
                return null;
            }
        };
        NativeBlock var = new NativeBlock(0) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                System.out.println(Arrays.toString(ctx.parent.param));
                return null;
            }
        };
        NativeBlock ret = new NativeBlock(0) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                return "Hallo Welt aus Deutschland";
            }
        };
        ByobBlock byob = new ByobBlock(2, new Block[]{var, var});
        byob.setParameter(0, ret);
        byob.setParameter(1, new ByobBlock(1, new Block[]{var, ret}));
        nb.setParameter(0, ret);
        IScript script = eng.compile(new Block[]{byob});
        for (int i = 0; i < 1; i++) {
            eng.execute(script);
        }
    }
}
