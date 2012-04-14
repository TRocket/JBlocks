package org.jblocks.scriptengine.impl;

import org.jblocks.scriptengine.Block;

/**
 *
 * A NativeBlock is <i>always</i> native. <br />
 * 
 * @author ZeroLuck
 */
class NativeBlock extends Block {

    public NativeBlock(int paramCount) {
        super(paramCount);
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

    /**
     * This has to be executed <b>very fast</b>. <br />
     * No sleeps! <br />
     */
    public Object evaluate(Object... params) {
        System.out.println("Hello");

        return null;
    }
}
