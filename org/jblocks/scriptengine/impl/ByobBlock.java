package org.jblocks.scriptengine.impl;

import org.jblocks.scriptengine.Block;

/**
 *
 * @author ZeroLuck
 */
class ByobBlock extends Block {

    private Block[] seq;
    private Object retValue;

    public ByobBlock(int paramCount, Block[] sequence) {
        super(paramCount);
        this.seq = sequence;
    }

    public Block[] getSequence() {
        return seq;
    }

    @Override
    public Object[] getParameters() {
        return super.getParameters();
    }

    @Override
    public Block clone() {
        int len = getParameterCount();
        NativeBlock n = new NativeBlock(len);
        for (int i = 0; i < len; i++) {
            Object o = getParameter(i);
            if (o instanceof NativeBlock) {
                n.setParameter(i, ((NativeBlock) o).clone());
            } else if (o instanceof Number | o instanceof String) {
                n.setParameter(i, o);
            } else {
                throw new IllegalStateException("can't clone parameter '" + o + "'");
            }
        }
        return n;
    }
}
