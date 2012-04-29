package org.jblocks.scriptengine;

/**
 * A NativeBlock is written in Java. <br />
 * 
 * @author ZeroLuck
 */
public class NativeBlock extends Block implements Executable {

    private Executable exec;

    public NativeBlock(int paramCount, long id) {
        super(paramCount, id);
    }

    private NativeBlock(int paramCount, long id, Executable e) {
        super(paramCount, id);
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
    public boolean equals(Object o) {
        if (!(o instanceof NativeBlock) || o == null || exec == null) {
            return super.equals(o);
        }
        NativeBlock block = (NativeBlock) o;
        return exec == block.exec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        if (exec == null) {
            return super.hashCode();
        } else {
            int hash = 3;
            hash = 31 * hash + this.exec.hashCode();
            return hash;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block clone() {
        int len = getParameterCount();
        NativeBlock n = new NativeBlock(len, getID(), exec == null ? this : exec);
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
