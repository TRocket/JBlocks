package org.jblocks.scriptengine.impl;

import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.Block.Default;
import org.jblocks.scriptengine.ByobBlock;
import org.jblocks.scriptengine.NativeBlock;
import org.jblocks.scriptengine.impl.DefaultScriptThread.StackElement;

/**
 *
 * @author ZeroLuck
 */
public class DefaultBlocks {

    // <global>
    public static final NativeBlock FOR;                     // 0: count                 1: sequence
    public static final NativeBlock RETURN;                  // 0: value to return
    public static final NativeBlock WHILE;                   // 0: expression (sequence) 1: sequence
    public static final NativeBlock IF;                      // 0: expression            1: sequence
    public static final NativeBlock IF_ELSE;                 // 0: expression            1: sequence1    2: sequence2
    public static final NativeBlock READ_GLOBAL_VARIABLE;    // 0: name
    public static final NativeBlock READ_PARAM_VARIABLE;     // 0: index
    public static final NativeBlock WRITE_GLOBAL_VARIABLE;   // 0: name                  1: value
    public static final NativeBlock WRITE_PARAM_VARIABLE;    // 0: index                 1: value
    public static final NativeBlock TRUE;                    // ---
    public static final NativeBlock FALSE;                   // ---
    public static final NativeBlock ADD;
    public static final NativeBlock SUB;
    public static final NativeBlock MUL;
    public static final NativeBlock DIV;
    public static final NativeBlock MOD;
    public static final NativeBlock SMALLER;
    public static final NativeBlock BIGGER;
    public static final NativeBlock EQUALS;
    public static final NativeBlock OR;
    public static final NativeBlock AND;
    public static final long PREFIX = 100;
    public static final long FOR_ID = PREFIX + 1;
    public static final long RETURN_ID = PREFIX + 2;
    public static final long WHILE_ID = PREFIX + 3;
    public static final long IF_ID = PREFIX + 4;
    public static final long IF_ELSE_ID = PREFIX + 5;
    public static final long READ_GLOBAL_VARIABLE_ID = PREFIX + 6;
    public static final long READ_PARAM_VARIABLE_ID = PREFIX + 7;
    public static final long WRITE_GLOBAL_VARIABLE_ID = PREFIX + 8;
    public static final long WRITE_PARAM_VARIABLE_ID = PREFIX + 9;
    public static final long TRUE_ID = PREFIX + 10;
    public static final long FALSE_ID = PREFIX + 11;
    public static final long ADD_ID = PREFIX + 12;
    public static final long SUB_ID = PREFIX + 13;
    public static final long MUL_ID = PREFIX + 14;
    public static final long DIV_ID = PREFIX + 15;
    public static final long MOD_ID = PREFIX + 16;
    public static final long SMALLER_ID = PREFIX + 17;
    public static final long BIGGER_ID = PREFIX + 18;
    public static final long EQUALS_ID = PREFIX + 19;
    public static final long OR_ID = PREFIX + 20;
    public static final long AND_ID = PREFIX + 21;
    // <private global>
    private final static Object[] empty = new Object[0];

    static {
        READ_GLOBAL_VARIABLE = new NativeBlock(1, READ_GLOBAL_VARIABLE_ID) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                if (params[0] == null) {
                    return null;
                }
                return ((StackElement) ctx).global.get("" + params[0]);
            }
        };
        WRITE_GLOBAL_VARIABLE = new NativeBlock(2, WRITE_GLOBAL_VARIABLE_ID) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                if (params[0] == null) {
                    return null;
                }
                ((StackElement) ctx).global.put("" + params[0], params[1]);
                return null;
            }
        };
        READ_PARAM_VARIABLE = new NativeBlock(1, READ_PARAM_VARIABLE_ID) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                if (params[0] == null) {
                    System.out.println("read param => null");
                    return null;
                }

                int index = (Integer) params[0];
                StackElement byob = ((StackElement) ctx);
                while (!(byob.perform instanceof ByobBlock)) {
                    byob = byob.parent;
                }
                System.out.println("read param =>>> " + byob.param[index]);
                return byob.param[index];
            }
        };
        WRITE_PARAM_VARIABLE = new NativeBlock(2, WRITE_PARAM_VARIABLE_ID) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                if (params[0] == null) {
                    return null;
                }

                int index = (Integer) params[0];
                StackElement byob = ((StackElement) ctx);
                while (!(byob.perform instanceof ByobBlock)) {
                    byob = byob.parent;
                }
                byob.param[index] = params[1];
                return null;
            }
        };
        FOR = new NativeBlock(3, FOR_ID) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                if (params[0] == null || params[1] == null) {
                    return null;
                }

                Block[] seq = (Block[]) params[1];
                int cnt = toInt(params[0]);
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
                me.param[1] = seq;
                me.param[0] = cnt;
                me.param[2] = off + 1;
                StackElement cmd = new StackElement(me, b, b.getParameters(), true, ((StackElement) ctx).global);
                if (cnt > 0) {
                    ((StackElement) ctx).parent = cmd;
                }
                return null;
            }
        };
        RETURN = new NativeBlock(1, RETURN_ID) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                System.out.println("return: " + params[0]);
                Object val = params[0];
                StackElement byob = ((StackElement) ctx);
                while (!(byob.perform instanceof ByobBlock)) {
                    byob = byob.parent;
                }
                if (byob.parent != null && byob.parent.doParam) {
                    byob.parent.param[byob.parent.off - 1] = val;
                }

                byob.off = byob.commands.length;
                ((StackElement) ctx).parent = byob.parent;
                return null;
            }
        };
        IF = new NativeBlock(3, IF_ID) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                Block[] seq = (Block[]) params[1];
                Object offObj = params[2];
                int off = 0;
                if (offObj != null) {
                    off = (Integer) offObj;
                }
                if (off >= seq.length || !toBoolean(params[0])) {
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
        IF_ELSE = new NativeBlock(4, IF_ELSE_ID) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                Block[] seq1 = (Block[]) params[1];
                Block[] seq2 = (Block[]) params[2];
                Block[] seq;
                if (!toBoolean(params[0])) {
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
        WHILE = new NativeBlock(4, WHILE_ID) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                if (params[0] == null) {
                    return null;
                }

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
                    StackElement me = new StackElement(((StackElement) ctx).parent, this, empty, false, ((StackElement) ctx).global);
                    me.param[0] = params[0];
                    me.param[1] = seq;
                    me.param[2] = true;
                    me.param[3] = off + 1;

                    if (off >= seq.length) {
                        ((StackElement) ctx).parent = me;
                        return null;
                    }
                    Block b = seq[off];
                    StackElement cmd = new StackElement(me, b, b.getParameters(), true, ((StackElement) ctx).global);

                    ((StackElement) ctx).parent = cmd;
                }
                return null;
            }
        };
        TRUE = new NativeBlock(0, TRUE_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return true;
            }
        };
        FALSE = new NativeBlock(0, FALSE_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return false;
            }
        };
        ADD = new NativeBlock(2, ADD_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                if (isInt(param[0]) && isInt(param[1])) {
                    return toInt(param[0]) + toInt(param[1]);
                } else {
                    return toDouble(param[0]) + toDouble(param[1]);
                }
            }
        };
        SUB = new NativeBlock(2, SUB_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                if (isInt(param[0]) && isInt(param[1])) {
                    return toInt(param[0]) - toInt(param[1]);
                } else {
                    return toDouble(param[0]) - toDouble(param[1]);
                }
            }
        };
        MUL = new NativeBlock(2, MUL_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                if (isInt(param[0]) && isInt(param[1])) {
                    return toInt(param[0]) * toInt(param[1]);
                } else {
                    return toDouble(param[0]) * toDouble(param[1]);
                }
            }
        };
        DIV = new NativeBlock(2, DIV_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                if (isInt(param[0]) && isInt(param[1])) {
                    return toInt(param[0]) / toInt(param[1]);
                } else {
                    return toDouble(param[0]) / toDouble(param[1]);
                }
            }
        };
        MOD = new NativeBlock(2, MOD_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                if (isInt(param[0]) && isInt(param[1])) {
                    return toInt(param[0]) % toInt(param[1]);
                } else {
                    return toDouble(param[0]) % toDouble(param[1]);
                }
            }
        };
        SMALLER = new NativeBlock(2, SMALLER_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return toDouble(param[0]) < toDouble(param[1]);
            }
        };
        BIGGER = new NativeBlock(2, BIGGER_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return toDouble(param[0]) > toDouble(param[1]);
            }
        };
        EQUALS = new NativeBlock(2, EQUALS_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                if (isInt(param[0]) && isInt(param[1])) {
                    return toInt(param[0]) == toInt(param[2]);
                } else {
                    return param[0].equals(param[1]);
                }
            }
        };
        OR = new NativeBlock(2, OR_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return toBoolean(param[0]) || toBoolean(param[1]);
            }
        };
        AND = new NativeBlock(2, AND_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return toBoolean(param[0]) && toBoolean(param[1]);
            }
        };
    }

    private static boolean toBoolean(Object o) {
        return o == null ? false : Boolean.parseBoolean(o + "");
    }

    private static double toDouble(Object o) {
        if (o instanceof Float) {
            return (Float) o;
        }
        if (o instanceof Double) {
            return (Double) o;
        }
        if (o instanceof Integer) {
            return (Integer) o;
        }
        try {
            return (Double.valueOf(("" + o).replace(',', '.')));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static boolean isInt(Object o) {
        if (o instanceof Integer) {
            return true;
        }
        try {
            Integer.parseInt("" + o);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static int toInt(Object o) {
        try {
            return (Double.valueOf(("" + o).replace(',', '.'))).intValue();
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static Block getDefaultBlock(Default def) {
        switch (def) {
            case FOR:
                return DefaultBlocks.FOR;
            case WHILE:
                return DefaultBlocks.WHILE;
            case RETURN:
                return DefaultBlocks.RETURN;
            case READ_GLOBAL_VARIABLE:
                return DefaultBlocks.READ_GLOBAL_VARIABLE;
            case READ_PARAM_VARIABLE:
                return DefaultBlocks.READ_PARAM_VARIABLE;
            case WRITE_GLOBAL_VARIABLE:
                return DefaultBlocks.WRITE_GLOBAL_VARIABLE;
            case WRITE_PARAM_VARIABLE:
                return DefaultBlocks.WRITE_PARAM_VARIABLE;
            case IF:
                return DefaultBlocks.IF;
            case IF_ELSE:
                return DefaultBlocks.IF_ELSE;
            case TRUE:
                return DefaultBlocks.TRUE;
            case FALSE:
                return DefaultBlocks.FALSE;
            case ADD:
                return DefaultBlocks.ADD;
            case SUB:
                return DefaultBlocks.SUB;
            case MUL:
                return DefaultBlocks.MUL;
            case DIV:
                return DefaultBlocks.DIV;
            case MOD:
                return DefaultBlocks.MOD;
            case SMALLER:
                return DefaultBlocks.SMALLER;
            case BIGGER:
                return DefaultBlocks.BIGGER;
            case EQUALS:
                return DefaultBlocks.EQUALS;
            case OR:
                return DefaultBlocks.OR;
            case AND:
                return DefaultBlocks.AND;
            default:
                return null;
        }
    }
}
