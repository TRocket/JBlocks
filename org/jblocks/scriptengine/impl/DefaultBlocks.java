package org.jblocks.scriptengine.impl;

import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.impl.DefaultScriptThread.StackElement;

/**
 *
 * @author ZeroLuck
 */
class DefaultBlocks {

    static NativeBlock FOR;
    static NativeBlock RETURN;
    static NativeBlock WHILE;
    static NativeBlock IF;
    static NativeBlock IF_ELSE;
    static NativeBlock READ_GLOBAL_VARIABLE;
    static NativeBlock READ_PARAM_VARIABLE;
    static NativeBlock WRITE_GLOBAL_VARIABLE;
    static NativeBlock WRITE_PARAM_VARIABLE;

    static {
        FOR = new NativeBlock(3) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                Block[] seq = (Block[]) params[0];
                int cnt = (Integer) params[1];
                Object offObj = params[2];
                int off = 0;
                if (offObj != null) {
                    off = (Integer) offObj;
                    if (off >= seq.length) {
                        off = 0;
                        cnt--;
                    }
                }
                Block b = seq[off];
                StackElement cmd = new StackElement(new StackElement(ctx.parent, this, new Object[]{params[0], cnt, off + 1}, true, ctx.global),
                        b, b.getParameters(), true, ctx.global);
                if (cnt > 0) {
                    ctx.parent = cmd;
                }
                return null;
            }
        };
        RETURN = new NativeBlock(1) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                Object val = params[0];
                System.out.println("return: " + val);
                StackElement byob = ctx;
                while (!(byob.perform instanceof ByobBlock)) {
                    byob = byob.parent;
                }
                if (byob.doParam) {
                    byob.param[byob.off] = val;
                }
                byob.off = byob.commands.length;
                return null;
            }
        };
        IF = new NativeBlock(3) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                Block[] seq = (Block[]) params[0];
                Object offObj = params[1];
                int off = 0;
                if (offObj != null) {
                    off = (Integer) offObj;
                    if (off >= seq.length) {
                        return null;
                    }
                }
                Block b = seq[off];
                StackElement cmd = new StackElement(new StackElement(ctx.parent, this, new Object[]{params[0], ++off}, false, ctx.global),
                        b, b.getParameters(), true, ctx.global);

                ctx.parent = cmd;


                return null;
            }
        };
        WHILE = new NativeBlock(2) {

            @Override
            public Object evaluate(StackElement ctx, Object... params) {
                Block exp = ((Block[]) params[0])[0];

                Block[] seq = (Block[]) params[1];
                Object offObj = params[1];
                int off = 0;
                if (offObj != null) {
                    off = (Integer) offObj;
                    if (off >= seq.length) {
                        off = 0;
                    }
                }
                Block b = seq[off];
                StackElement cmd = new StackElement(new StackElement(ctx.parent, this, new Object[]{params[0], seq, ++off}, true, ctx.global),
                        b, b.getParameters(), true, ctx.global);

                ctx.parent = cmd;
                return null;
            }
        };
    }
}
