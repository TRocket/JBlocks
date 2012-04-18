package org.jblocks.scriptengine;

/**
 * A NativeBlock is written in Java. <br />
 * 
 * @author ZeroLuck
 */
public class NativeBlock extends Block implements Executable {

    private Executable exec;

    public NativeBlock(int paramCount) {
        super(paramCount);
    }

    private NativeBlock(int paramCount, Executable e) {
        super(paramCount);
        exec = e;
    }

    /**
     * This has to be executed <b>fast</b>. <br />
     * This code can block the thread-scheduler. <br />
     */
    @Override
    public Object evaluate(Object ctx, Object... params) {
        return exec.evaluate(ctx, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block clone() {
        int len = getParameterCount();
        NativeBlock n = new NativeBlock(len, this);
        for (int i = 0; i < len; i++) {
            Object o = getParameter(i);
            if (o instanceof Block) {
                n.setParameter(i, ((Block) o).clone());
            } else {
                n.setParameter(i, o);
            }
        }
        return n;
    }

    @Override
    public String toString() {
        return "NativeBlock";
    }
}
