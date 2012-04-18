package org.jblocks.scriptengine.impl;

import org.jblocks.scriptengine.NativeBlock;
import org.jblocks.scriptengine.ByobBlock;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.impl.DefaultScriptThread.StackElement;

/**
 *
 * @author ZeroLuck
 */
class DefaultBlocks {

    // <global>
    static final NativeBlock FOR;                     // 0: sequence              1: count
    static final NativeBlock RETURN;                  // 0: value to return
    static final NativeBlock WHILE;                   // 0: expression (sequence) 1: sequence
    static final NativeBlock IF;                      // 0: expression            1: sequence
    static final NativeBlock IF_ELSE;                 // 0: expression            1: sequence1    2: sequence2
    static final NativeBlock READ_GLOBAL_VARIABLE;    // 0: name
    static final NativeBlock READ_PARAM_VARIABLE;     // 0: index
    static final NativeBlock WRITE_GLOBAL_VARIABLE;   // 0: name                  1: value
    static final NativeBlock WRITE_PARAM_VARIABLE;    // 0: index                 1: value
    // <private global>
    private final static Object[] empty = new Object[0];

    static {
        READ_GLOBAL_VARIABLE = new NativeBlock(1) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                if (params[0] == null) {
                    return null;
                }
                return ((StackElement) ctx).global.get("" + params[0]);
            }
        };
        WRITE_GLOBAL_VARIABLE = new NativeBlock(2) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                if (params[0] == null) {
                    return null;
                }
                ((StackElement) ctx).global.put("" + params[0], params[1]);
                return null;
            }
        };
        READ_PARAM_VARIABLE = new NativeBlock(1) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                int index = (Integer) params[0];
                StackElement byob = ((StackElement) ctx);
                while (!(byob.perform instanceof ByobBlock)) {
                    byob = byob.parent;
                }
                return byob.param[index];
            }
        };
        WRITE_PARAM_VARIABLE = new NativeBlock(2) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                int index = (Integer) params[0];
                StackElement byob = ((StackElement) ctx);
                while (!(byob.perform instanceof ByobBlock)) {
                    byob = byob.parent;
                }
                byob.param[index] = params[1];
                return null;
            }
        };
        FOR = new NativeBlock(3) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
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
                } else if (off >= seq.length) {
                    return null;
                }

                Block b = seq[off];
                StackElement me = new StackElement(((StackElement) ctx).parent, this, empty, false, ((StackElement) ctx).global);
                me.param[0] = seq;
                me.param[1] = cnt;
                me.param[2] = off + 1;
                StackElement cmd = new StackElement(me, b, b.getParameters(), true, ((StackElement) ctx).global);
                if (cnt > 0) {
                    ((StackElement) ctx).parent = cmd;
                }
                return null;
            }
        };
        RETURN = new NativeBlock(1) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                Object val = params[0];
                StackElement byob = ((StackElement) ctx);
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
            public Object evaluate(Object ctx, Object... params) {
                Block[] seq = (Block[]) params[1];
                Object offObj = params[2];
                int off = 0;
                if (offObj != null) {
                    off = (Integer) offObj;
                }
                if (off >= seq.length || !(params[0] instanceof Boolean) || ((Boolean) params[0]) != Boolean.TRUE) {
                    return null;
                }
                Block b = seq[off];
                StackElement me = new StackElement(((StackElement) ctx).parent, this, empty, false, ((StackElement) ctx).global);
                me.param[0] = true;
                me.param[1] = seq;
                me.param[2] = off + 1;

                ((StackElement) ctx).parent = new StackElement(me, b, b.getParameters(), true, ((StackElement) ctx).global);
                return null;
            }
        };
        IF_ELSE = new NativeBlock(4) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                Block[] seq1 = (Block[]) params[1];
                Block[] seq2 = (Block[]) params[2];
                Block[] seq;
                if (!(params[0] instanceof Boolean) || ((Boolean) params[0]) != Boolean.TRUE) {
                    seq = seq2;
                } else {
                    seq = seq1;
                }
                Object offObj = params[3];
                int off = 0;
                if (offObj != null) {
                    off = (Integer) offObj;
                }
                if (off >= seq.length) {
                    return null;
                }
                Block b = seq[off];
                StackElement me = new StackElement(((StackElement) ctx).parent, this, empty, false, ((StackElement) ctx).global);
                me.param[0] = seq == seq1;
                me.param[1] = seq1;
                me.param[2] = seq2;
                me.param[3] = off + 1;

                ((StackElement) ctx).parent = new StackElement(me, b, b.getParameters(), true, ((StackElement) ctx).global);
                return null;
            }
        };
        WHILE = new NativeBlock(4) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                Block exp = ((Block[]) params[0])[0];

                if (params[2] == null) {
                    StackElement me = new StackElement(((StackElement) ctx).parent, this, empty, true, ((StackElement) ctx).global);
                    me.off = 3;
                    me.param[0] = params[0];
                    me.param[1] = params[1];
                    me.param[2] = false;
                    me.param[3] = null;
                    ((StackElement) ctx).parent = new StackElement(me, exp, exp.getParameters(), true, ((StackElement) ctx).global);

                } else if (params[2] == Boolean.TRUE) {
                    Block[] seq = (Block[]) params[1];
                    Object offObj = params[3];
                    int off = 0;
                    if (offObj != null) {
                        off = (Integer) offObj;
                        if (off >= seq.length) {
                            StackElement me = new StackElement(((StackElement) ctx).parent, this, empty, false, ((StackElement) ctx).global);
                            me.param[0] = params[0];
                            me.param[1] = seq;
                            me.param[2] = null;
                            me.param[3] = null;
                            ((StackElement) ctx).parent = me;
                            return null;
                        }
                    }
                    Block b = seq[off];
                    StackElement me = new StackElement(((StackElement) ctx).parent, this, empty, false, ((StackElement) ctx).global);
                    me.param[0] = params[0];
                    me.param[1] = seq;
                    me.param[2] = true;
                    me.param[3] = off + 1;
                    StackElement cmd = new StackElement(me, b, b.getParameters(), true, ((StackElement) ctx).global);

                    ((StackElement) ctx).parent = cmd;
                }
                return null;
            }
        };
    }
}
