package org.jblocks.scriptengine.impl;

import java.util.Arrays;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.IScript;

/**
 *
 * @author ZeroLuck
 */
public class Test {

    public static void main(String[] args) {
        DefaultScriptEngine eng = new DefaultScriptEngine();
        NativeBlock nb = new NativeBlock(1) {

            @Override
            public Object evaluate(Object... params) {
                System.out.println(Arrays.toString(params));
                return null;
            }
        };
        NativeBlock rep = new NativeBlock(2) {

            @Override
            public Object evaluate(Object... params) {
                return "1: --->>" + params[0] + "  2: ---->>" + params[1];
            }
        };
        nb.setParameter(0, rep);
        IScript script = eng.compile(new Block[]{nb, nb, nb});
        for (int i = 0; i < 4; i++) {
            eng.execute(script);
        }
    }
}
