package org.jblocks.scriptengine;

/**
 * A NativeBlock is written in Java. <br />
 * 
 * @author ZeroLuck
 */
public abstract class NativeBlock extends Block implements Executable {

    @SuppressWarnings("LeakingThisInConstructor")
    public NativeBlock(int paramCount, long id) {
        super(paramCount, id);
    }

    /**
     * This has to be executed <b>fast</b>. <br />
     * This code can block the thread-scheduler. <br />
     */
    @Override
    public abstract Object evaluate(Object ctx, Object... params);

    /**
     * {@inheritDoc}
     */
    @Override
    public Block clone() {
        int len = getParameterCount();
        final NativeBlock src = this;
        NativeBlock n = new NativeBlock(len, getID()) {

            @Override
            public Object evaluate(Object ctx, Object... params) {
                return src.evaluate(ctx, params);
            }

            @Override
            public Block clone() {
                Block b = (NativeBlock) src.clone();
                Block.copyParameters(this, b);
                return b;
            }
        };
        Block.copyParameters(this, n);
        return n;
    }

    @Override
    public String toString() {
        return "NativeBlock[" + getID() + "]";
    }
}
